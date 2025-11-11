package format.backend.submission.service;

import format.backend.auth.entity.Role;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.repository.FormRepository;
import format.backend.form.service.FormService;
import format.backend.submission.dto.SubmissionAnswerRequestDto;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.exception.NotExistingQuestionsAnswersException;
import format.backend.submission.exception.RequiredQuestionsNotAnsweredException;
import format.backend.submission.exception.SubmissionAlreadyCreatedForUserException;
import format.backend.submission.exception.SubmissionAnswersValidationException;
import format.backend.submission.exception.SubmissionNotFoundException;
import format.backend.submission.exception.SubmissionNotFoundForUserException;
import format.backend.submission.mapper.SubmissionMapper;
import format.backend.submission.repository.SubmissionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final FormRepository formRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final UserService userService;
    private final FormService formService;

    public Page<SubmissionResponseDto> findAllByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, Pageable pageable) {
        val form = formService.findOrThrow(formIdOrSlug);

        val isFormOwner = Optional.ofNullable(form.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner || !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val submissions = submissionRepository.findAllByFormId(
                form.getId(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("_id"))));

        return submissions.map(submissionMapper::toResponseDto);
    }

    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        val submission = submissionRepository
                .findByFormIdAndAuthorId(form.getId(), keycloakJwtClaims.sub())
                .orElseThrow(() -> new SubmissionNotFoundForUserException(formIdOrSlug));

        return submissionMapper.toResponseDto(submission);
    }

    // FIXME:
    @SuppressWarnings("java:S3776")
    private Map<String, List<String>> validateSubmissionAnswers(
            List<SubmissionAnswerRequestDto> submissionAnswers, Map<String, QuestionEntity> questionsById) {
        val errors = new HashMap<String, List<String>>();
        for (var i = 0; i < submissionAnswers.size(); i++) {
            val submissionAnswer = submissionAnswers.get(i);
            val question = questionsById.get(submissionAnswer.questionId());
            val answersIds =
                    question.getAnswers().stream().map(AnswerEntity::getId).collect(Collectors.toUnmodifiableSet());

            switch (question.getType()) {
                case SINGLE_CHOICE -> {
                    val errorsList = new ArrayList<String>();

                    if (submissionAnswer.chosenAnswerIds().size() != 1)
                        errorsList.add("Question must have exactly one answer");
                    if (!answersIds.containsAll(submissionAnswer.chosenAnswerIds())) {
                        val invalidAnswerIds = submissionAnswer.chosenAnswerIds().stream()
                                .filter(a -> !answersIds.contains(a))
                                .toList();

                        errorsList.add(String.format(
                                "Given answer ids %s are not valid. Valid ids are %s",
                                String.join(", ", invalidAnswerIds), String.join(", ", answersIds)));
                    }

                    if (!errorsList.isEmpty()) errors.put(String.format("answers[%s].chosenAnswerIds", i), errorsList);
                }
                case MULTIPLE_CHOICE -> {
                    val errorsList = new ArrayList<String>();

                    if (submissionAnswer.chosenAnswerIds().isEmpty()
                            || submissionAnswer.chosenAnswerIds().size()
                                    > question.getAnswers().size())
                        errorsList.add(String.format(
                                "Question must have between 1 and %s answers",
                                question.getAnswers().size()));
                    if (!answersIds.containsAll(submissionAnswer.chosenAnswerIds())) {
                        val invalidAnswerIds = submissionAnswer.chosenAnswerIds().stream()
                                .filter(a -> !answersIds.contains(a))
                                .toList();

                        errorsList.add(String.format(
                                "Given answer ids %s are not valid. Valid ids are %s",
                                String.join(", ", invalidAnswerIds), String.join(", ", answersIds)));
                    }

                    if (!errorsList.isEmpty()) errors.put(String.format("answers[%s].chosenAnswerIds", i), errorsList);
                }
                case OPEN -> {
                    if (submissionAnswer.openAnswer() == null
                            || submissionAnswer.openAnswer().isBlank())
                        errors.put(
                                String.format("answers[%s].openAnswer", i),
                                List.of("Question must have non-blank open answer"));
                }
            }
        }

        return errors;
    }

    @Transactional
    public SubmissionResponseDto createByFormIdOrSlug(
            @Nullable KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, SubmissionRequestDto requestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getAllowsGuestSubmissions() && keycloakJwtClaims == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        val questionsById = form.getQuestions().stream()
                .collect(Collectors.toUnmodifiableMap(QuestionEntity::getId, Function.identity()));
        val questionIds = questionsById.keySet();

        val nonExistingQuestionIds = requestDto.answers().stream()
                .map(SubmissionAnswerRequestDto::questionId)
                .filter(q -> !questionIds.contains(q))
                .toList();
        if (!nonExistingQuestionIds.isEmpty())
            throw new NotExistingQuestionsAnswersException(formIdOrSlug, nonExistingQuestionIds);

        val notAnsweredRequiredQuestionIds = form.getQuestions().stream()
                .filter(QuestionEntity::getIsRequired)
                .map(QuestionEntity::getId)
                .filter(id -> requestDto.answers().stream()
                        .noneMatch(a -> a.questionId().equals(id)))
                .toList();
        if (!notAnsweredRequiredQuestionIds.isEmpty())
            throw new RequiredQuestionsNotAnsweredException(notAnsweredRequiredQuestionIds);

        val errors = validateSubmissionAnswers(requestDto.answers(), questionsById);
        if (!errors.isEmpty()) throw new SubmissionAnswersValidationException(errors);

        val user = Optional.ofNullable(keycloakJwtClaims)
                .map(claims -> userService.findOrThrow(claims.sub()))
                .orElse(null);
        val submissionEntity = submissionMapper.toEntity(requestDto, form, user);
        for (val submissionAnswer : submissionEntity.getAnswers()) {
            val question = questionsById.get(submissionAnswer.getQuestionId());

            switch (question.getType()) {
                case SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                    val questionsAnswersIds = question.getAnswers().stream()
                            .map(AnswerEntity::getId)
                            .collect(Collectors.toUnmodifiableSet());

                    submissionAnswer.setOpenAnswer(null);
                    submissionAnswer.setChosenAnswerIds(submissionAnswer.getChosenAnswerIds().stream()
                            .filter(questionsAnswersIds::contains)
                            .collect(Collectors.toUnmodifiableSet()));
                }
                case OPEN -> submissionAnswer.getChosenAnswerIds().clear();
            }
        }

        try {
            val savedSubmissionEntity = submissionRepository.save(submissionEntity);

            form.setSubmissionsCount(form.getSubmissionsCount() + 1);
            formRepository.save(form);

            return submissionMapper.toResponseDto(savedSubmissionEntity);
        } catch (DataIntegrityViolationException e) {
            throw new SubmissionAlreadyCreatedForUserException(formIdOrSlug);
        }
    }

    @Transactional
    public void delete(KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, String submissionId) {
        val form = formService.findOrThrow(formIdOrSlug);
        val submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));

        val isFormOwner = Optional.ofNullable(form.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner || !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        submissionRepository.delete(submission);
        form.setSubmissionsCount(form.getSubmissionsCount() - 1);
        formRepository.save(form);
    }
}

package format.backend.submission.service;

import format.backend.auth.entity.Role;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.core.exception.ValidationException;
import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.service.FormService;
import format.backend.submission.SubmissionValidator;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.exception.SubmissionAlreadyCreatedForUserException;
import format.backend.submission.exception.SubmissionNotFoundException;
import format.backend.submission.exception.SubmissionNotFoundForUserException;
import format.backend.submission.mapper.SubmissionMapper;
import format.backend.submission.repository.SubmissionRepository;
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

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionValidator submissionValidator;
    private final FormService formService;
    private final UserService userService;

    public Page<SubmissionResponseDto> findAllByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, Pageable pageable) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getSaveSubmissions()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val isFormOwner = Optional.ofNullable(form.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val submissions = submissionRepository.findAllByFormId(
                form.getId(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("_id"))));

        return submissions.map(submissionMapper::toResponseDto);
    }

    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getSaveSubmissions()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val submission = submissionRepository
                .findByFormIdAndAuthorId(form.getId(), keycloakJwtClaims.sub())
                .orElseThrow(() -> new SubmissionNotFoundForUserException(formIdOrSlug));

        return submissionMapper.toResponseDto(submission);
    }

    @Transactional
    public SubmissionResponseDto createByFormIdOrSlug(
            @Nullable KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, SubmissionRequestDto requestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getAllowsGuestSubmissions() && keycloakJwtClaims == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!form.getSaveSubmissions()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val errors = submissionValidator.validate(form, requestDto);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val user = Optional.ofNullable(keycloakJwtClaims)
                .map(claims -> userService.findOrThrow(claims.sub()))
                .orElse(null);
        val submissionEntity = submissionMapper.toEntity(requestDto, form, user);

        val questionsById = form.getQuestions().stream()
                .collect(Collectors.toUnmodifiableMap(QuestionEntity::getId, Function.identity()));
        for (val submissionAnswer : submissionEntity.getAnswers()) {
            val question = questionsById.get(submissionAnswer.getQuestionId());

            switch (question.getType()) {
                case SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                    val questionAnswersIds = question.getAnswers().stream()
                            .map(AnswerEntity::getId)
                            .collect(Collectors.toUnmodifiableSet());
                    val existingAnswersIds = submissionAnswer.getChosenAnswerIds().stream()
                            .filter(questionAnswersIds::contains)
                            .collect(Collectors.toUnmodifiableSet());

                    submissionAnswer.getChosenAnswerIds().clear();
                    submissionAnswer.getChosenAnswerIds().addAll(existingAnswersIds);
                    submissionAnswer.setOpenAnswer(null);
                }
                case OPEN -> submissionAnswer.getChosenAnswerIds().clear();
            }
        }

        try {
            val savedSubmissionEntity = submissionRepository.save(submissionEntity);
            formService.incrementSubmissionsCountById(form.getId());

            return submissionMapper.toResponseDto(savedSubmissionEntity);
        } catch (DataIntegrityViolationException e) {
            throw new SubmissionAlreadyCreatedForUserException(formIdOrSlug);
        }
    }

    @Transactional
    public void delete(KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, String submissionId) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getSaveSubmissions()) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));

        val isFormOwner = Optional.ofNullable(form.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        submissionRepository.delete(submission);
        formService.decrementSubmissionsCountById(form.getId());
    }
}

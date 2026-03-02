package format.backend.submission.service;

import format.backend.auth.entity.Role;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.core.exception.ValidationException;
import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.service.FormService;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.dto.SubmissionStatisticsResponseDto;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.exception.SubmissionAlreadyCreatedForUserException;
import format.backend.submission.exception.SubmissionNotFoundException;
import format.backend.submission.exception.SubmissionNotFoundForUserException;
import format.backend.submission.exception.SubmissionOperationNotSupported;
import format.backend.submission.mapper.SubmissionMapper;
import format.backend.submission.repository.SubmissionRepository;
import format.backend.submission.validator.SubmissionValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final MongoTemplate mongoTemplate;

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionValidator submissionValidator;

    private final FormService formService;
    private final UserService userService;

    private static final String FORM_ID_FIELD = "formId";
    private static final String QUESTION_ID_FIELD = "questionId";
    private static final String CHOSEN_ANSWER_IDS_FIELD = "answers.chosenAnswerIds";

    private void isOwnerOrAdminCheck(Optional<String> authorId, KeycloakJwtClaims keycloakJwtClaims) {
        val isFormOwner =
                authorId.map(a -> Objects.equals(a, keycloakJwtClaims.sub())).orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public Page<@NonNull SubmissionResponseDto> findAllByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, Pageable pageable) {
        val form = formService.findOrThrow(formIdOrSlug);
        isOwnerOrAdminCheck(Optional.ofNullable(form.getAuthorId()), keycloakJwtClaims);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val countOperations = List.of(
                Aggregation.match(Criteria.where(FORM_ID_FIELD).is(form.getId())),
                Aggregation.count().as("count"));

        final long total = Optional.ofNullable(mongoTemplate
                        .aggregate(Aggregation.newAggregation(countOperations), SubmissionEntity.class, Document.class)
                        .getUniqueMappedResult())
                .map(d -> (long) d.getInteger("count"))
                .orElse(0L);
        if (total == 0) return Page.empty(pageable);

        val operations = new ArrayList<AggregationOperation>();

        operations.add(Aggregation.match(Criteria.where(FORM_ID_FIELD).is(form.getId())));
        operations.add(Aggregation.sort(Sort.by(Sort.Order.desc("_id"))));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        operations.add(Aggregation.lookup("users", "authorId", "_id", "author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        val results = mongoTemplate
                .aggregate(Aggregation.newAggregation(operations), SubmissionEntity.class, SubmissionResponseDto.class)
                .getMappedResults();

        return new PageImpl<>(results, pageable, total);
    }

    public SubmissionResponseDto findByFormIdOrSlugAndSubmissionId(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, String submissionId) {
        val form = formService.findOrThrow(formIdOrSlug);
        isOwnerOrAdminCheck(Optional.ofNullable(form.getAuthorId()), keycloakJwtClaims);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val submission = submissionRepository
                .findByIdAndFormId(submissionId, form.getId())
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));
        val authorName = Optional.ofNullable(submission.getAuthorId())
                .map(authorId -> userService.findOrThrow(authorId).getUsername())
                .orElse(null);

        return submissionMapper.toResponseDto(submission, authorName);
    }

    public List<SubmissionStatisticsResponseDto> findSubmissionsStatisticsByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        isOwnerOrAdminCheck(Optional.ofNullable(form.getAuthorId()), keycloakJwtClaims);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val operations = new ArrayList<AggregationOperation>();

        operations.add(Aggregation.match(Criteria.where(FORM_ID_FIELD)
                .is(form.getId())
                .and("answers.chosenAnswerIds.0")
                .exists(true)));
        operations.add(Aggregation.unwind("answers"));
        operations.add(Aggregation.match(Criteria.where(CHOSEN_ANSWER_IDS_FIELD).ne(List.of())));
        operations.add(Aggregation.unwind(CHOSEN_ANSWER_IDS_FIELD));
        operations.add(Aggregation.group("answers.questionId", CHOSEN_ANSWER_IDS_FIELD)
                .count()
                .as("totalCount"));
        operations.add(Aggregation.project()
                .and("_id.questionId")
                .as(QUESTION_ID_FIELD)
                .and("_id.chosenAnswerIds")
                .as("entry.answerId")
                .and("totalCount")
                .as("entry.totalCount")
                .andExclude("_id"));
        operations.add(Aggregation.group(QUESTION_ID_FIELD).push("entry").as("submissionStatistics"));
        operations.add(Aggregation.project().and("_id").as(QUESTION_ID_FIELD).andInclude("submissionStatistics"));

        val results = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), SubmissionEntity.class, SubmissionStatisticsResponseDto.class);

        return results.getMappedResults();
    }

    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val submission = submissionRepository
                .findByFormIdAndAuthorId(form.getId(), keycloakJwtClaims.sub())
                .orElseThrow(() -> new SubmissionNotFoundForUserException(formIdOrSlug));
        return submissionMapper.toResponseDto(submission, keycloakJwtClaims.username());
    }

    @Transactional
    public SubmissionResponseDto createByFormIdOrSlug(
            @Nullable KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, SubmissionRequestDto requestDto) {
        val form = formService.findOrThrow(formIdOrSlug);

        if (!form.getAllowsGuestSubmissions() && keycloakJwtClaims == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val errors = submissionValidator.validate(form, requestDto);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val userId = Optional.ofNullable(keycloakJwtClaims)
                .map(KeycloakJwtClaims::sub)
                .orElse(null);
        val submissionEntity = submissionMapper.toEntity(requestDto, form.getId(), userId);

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

            val authorName = Optional.ofNullable(keycloakJwtClaims)
                    .map(KeycloakJwtClaims::username)
                    .orElse(null);
            return submissionMapper.toResponseDto(savedSubmissionEntity, authorName);
        } catch (DataIntegrityViolationException e) {
            throw new SubmissionAlreadyCreatedForUserException(formIdOrSlug);
        }
    }

    @Transactional
    public void delete(KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, String submissionId) {
        val form = formService.findOrThrow(formIdOrSlug);
        isOwnerOrAdminCheck(Optional.ofNullable(form.getAuthorId()), keycloakJwtClaims);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val submission = submissionRepository
                .findByIdAndFormId(submissionId, form.getId())
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));

        submissionRepository.delete(submission);
        formService.decrementSubmissionsCountById(form.getId());
    }
}

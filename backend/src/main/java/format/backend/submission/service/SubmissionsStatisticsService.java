package format.backend.submission.service;

import format.backend.auth.entity.Role;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.QuestionType;
import format.backend.form.service.FormService;
import format.backend.submission.dto.SubmissionsStatisticsResponseDto;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import format.backend.submission.exception.SubmissionOperationNotSupported;
import format.backend.submission.mapper.SubmissionsStatisticsMapper;
import format.backend.submission.repository.SubmissionsStatisticsRepository;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SubmissionsStatisticsService {

    @Lazy
    private final FormService formService;

    private final MongoTemplate mongoTemplate;

    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;
    private final SubmissionsStatisticsMapper submissionsStatisticsMapper;

    public SubmissionsStatisticsResponseDto findByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        val formId = form.getId();

        val isFormOwner = Objects.equals(form.getAuthorId(), keycloakJwtClaims.sub());
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (!form.getSaveSubmissions() || form.getAuthorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        val submissionsStatisticsEntity =
                submissionsStatisticsRepository.findById(formId).orElse(new SubmissionsStatisticsEntity(formId));
        return submissionsStatisticsMapper.toDto(submissionsStatisticsEntity, form.getSubmissionsCount());
    }

    @Transactional
    public void create(FormEntity form) {
        val submissionsStatistics = new SubmissionsStatisticsEntity(form.getId());
        for (val question : form.getQuestions()) {
            if (question.getType() == QuestionType.OPEN) continue;

            val answersStatistics =
                    question.getAnswers().stream().collect(Collectors.toUnmodifiableMap(AnswerEntity::getId, _ -> 0L));
            submissionsStatistics
                    .getQuestions()
                    .put(question.getId(), new SubmissionsStatisticsEntity.Statistics(answersStatistics));
        }

        submissionsStatisticsRepository.save(submissionsStatistics);
    }

    private void update(SubmissionEntity submission, int delta) {
        val update = new Update();

        for (val answer : submission.getAnswers()) {
            for (val chosenAnswerId : answer.getChosenAnswerIds()) {
                val path = SubmissionsStatisticsEntity.getPath(answer.getQuestionId(), chosenAnswerId);
                update.inc(path, delta);
            }
        }

        if (update.getUpdateObject().isEmpty()) return;

        mongoTemplate.upsert(
                Query.query(Criteria.where("formId").is(submission.getFormId())),
                update,
                SubmissionsStatisticsEntity.class);
    }

    @Transactional
    public void increment(SubmissionEntity submission) {
        update(submission, 1);
    }

    @Transactional
    public void decrement(SubmissionEntity submission) {
        update(submission, -1);
    }

    @Transactional
    public void delete(String formId) {
        submissionsStatisticsRepository.deleteById(formId);
    }

    @Transactional
    @SuppressWarnings("java:S6809")
    public void reset(FormEntity form) {
        delete(form.getId());
        create(form);
    }
}

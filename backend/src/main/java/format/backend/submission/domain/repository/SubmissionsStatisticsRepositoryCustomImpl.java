package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
class SubmissionsStatisticsRepositoryCustomImpl implements SubmissionsStatisticsRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void update(SubmissionEntity submissionEntity, int delta) {
        val update = new Update();

        for (val answer : submissionEntity.getAnswers()) {
            for (val chosenAnswerId : answer.getChosenAnswerIds()) {
                val path = SubmissionsStatisticsEntity.getQuestionAnswerPath(answer.getQuestionId(), chosenAnswerId);
                update.inc(path, delta);
            }
        }

        if (update.getUpdateObject().isEmpty()) return;

        mongoTemplate.upsert(
                Query.query(
                        Criteria.where(SubmissionsStatisticsEntity::getFormId).is(submissionEntity.getFormId())),
                update,
                SubmissionsStatisticsEntity.class);
    }

    @Override
    public void deleteQuestionsByFormIdAndQuestionIdIn(String formId, Collection<String> questionIds) {
        val formIdQuery = Query.query(
                Criteria.where(SubmissionsStatisticsEntity::getFormId).is(formId));
        val update = new Update();
        questionIds.forEach(id -> update.unset(SubmissionsStatisticsEntity.getQuestionPath(id)));

        mongoTemplate.updateFirst(formIdQuery, update, SubmissionsStatisticsEntity.class);
    }
}

package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionAnswerEntity;
import format.backend.submission.domain.entity.SubmissionEntity;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
class SubmissionRepositoryCustomImpl implements SubmissionRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<SubmissionListProjection> findAllByFormId(String formId, Pageable pageable) {
        val formIdCriteria = Criteria.where(SubmissionEntity::getFormId).is(formId);

        val total = mongoTemplate.count(Query.query(formIdCriteria), SubmissionEntity.class);
        if (total == 0) return Page.empty(pageable);

        val operations = new ArrayList<AggregationOperation>();

        operations.add(Aggregation.match(formIdCriteria));
        operations.add(Aggregation.sort(Sort.by(Sort.Order.desc(SubmissionEntity::getId))));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));
        operations.add(Aggregation.lookup("users", "authorId", "_id", "author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        val submissions = mongoTemplate
                .aggregate(
                        Aggregation.newAggregation(operations), SubmissionEntity.class, SubmissionListProjection.class)
                .getMappedResults();

        return new PageImpl<>(submissions, pageable, total);
    }

    @Override
    public long deleteAnswersByFormIdAndQuestionIdIn(String formId, Collection<String> questionIds) {
        val formIdQuery =
                Query.query(Criteria.where(SubmissionEntity::getFormId).is(formId));
        val pull = new Update()
                .pull(
                        SubmissionEntity::getAnswers,
                        Criteria.where(SubmissionAnswerEntity::getQuestionId).in(questionIds));
        mongoTemplate.updateMulti(formIdQuery, pull, SubmissionEntity.class);

        val emptyAnswersQuery = Query.query(Criteria.where(SubmissionEntity::getFormId)
                .is(formId)
                .and(SubmissionEntity::getAnswers)
                .size(0));
        return mongoTemplate.remove(emptyAnswersQuery, SubmissionEntity.class).getDeletedCount();
    }
}

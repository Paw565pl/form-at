package format.backend.formcomment.domain.repository;

import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
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

@RequiredArgsConstructor
class FormCommentRepositoryCustomImpl implements FormCommentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<FormCommentListProjection> findAll(@Nullable String userId, String formId, Pageable pageable) {
        val formIdCriteria = Criteria.where(FormCommentEntity::getFormId).is(formId);

        val total = mongoTemplate.count(Query.query(formIdCriteria), FormCommentEntity.class);
        if (total == 0) return Page.empty(pageable);

        val operations = new ArrayList<AggregationOperation>();

        operations.add(Aggregation.match(formIdCriteria));
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, FormCommentEntity::getId)));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        operations.add(Aggregation.lookup()
                .from("users")
                .localField("authorId")
                .foreignField("_id")
                .as("author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        if (userId != null) {
            operations.add(Aggregation.lookup()
                    .from("formCommentRatings")
                    .localField("_id")
                    .foreignField("commentId")
                    .pipeline(Aggregation.match(
                            Criteria.where(FormCommentRatingEntity::getAuthorId).is(userId)))
                    .as("userRatings"));

            operations.add(Aggregation.addFields()
                    .addField("userRating")
                    .withValue(ArrayOperators.arrayOf("userRatings.type").first())
                    .build());
        }

        val comments = mongoTemplate
                .aggregate(
                        Aggregation.newAggregation(operations),
                        FormCommentEntity.class,
                        FormCommentListProjection.class)
                .getMappedResults();

        return new PageImpl<>(comments, pageable, total);
    }
}

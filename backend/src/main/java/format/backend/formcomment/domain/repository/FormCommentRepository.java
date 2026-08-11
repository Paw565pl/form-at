package format.backend.formcomment.domain.repository;

import format.backend.formcomment.domain.entity.FormCommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FormCommentRepository extends MongoRepository<FormCommentEntity, String>, FormCommentRepositoryCustom {

    @Query("{ '_id': ?0 }")
    @Update("{ $inc: { 'ratingScore': ?1 } }")
    long updateRatingScore(String id, long ratingScoreDelta);

    long countAllByAuthorId(String authorId);

    long deleteAllByFormId(String formId);
}

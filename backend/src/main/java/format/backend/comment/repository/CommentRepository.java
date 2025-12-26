package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    Page<CommentEntity> findAllByFormId(String formId, Pageable pageable);

    void deleteAllByFormId(String formId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingScore': ?1 } }")
    void updateRatingScore(String commentId, int delta);
}

package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CommentRepository extends MongoRepository<@NonNull CommentEntity, @NonNull String> {
    Integer countAllByAuthorId(String authorId);

    void deleteAllByFormId(String formId);

    List<CommentEntity> findAllByFormId(String formId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingScore': 1 } }")
    void incrementRatingScore(String commentId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingScore': -1 } }")
    void decrementRatingScore(String commentId);
}

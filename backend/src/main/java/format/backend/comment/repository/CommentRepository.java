package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CommentRepository extends MongoRepository<@NonNull CommentEntity, @NonNull String> {
    long countAllByAuthorId(String authorId);

    List<CommentEntity> deleteAllByFormId(String formId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingScore': ?1 } }")
    void updateRatingScore(String commentId, int delta);
}

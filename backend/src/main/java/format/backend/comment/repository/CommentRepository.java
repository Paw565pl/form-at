package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {
    Integer countAllByAuthorId(String authorId);

    @Query(value = "{ 'form.$id': ?0 }", fields = "{ '_id' : 1 }")
    List<String> findAllIdsByFormId(String formId);

    void deleteAllByFormId(String formId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'ratingScore': ?1 } }")
    void updateRatingScore(String commentId, int delta);
}

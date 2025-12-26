package format.backend.comment_rating.repository;

import format.backend.comment_rating.entity.CommentRatingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.Optional;

public interface CommentRatingRepository extends MongoRepository<CommentRatingEntity, String> {
    Optional<CommentRatingEntity> findByCommentIdAndAuthorId(String commentId, String authorId);

    void deleteAllByCommentId(String commentId);

    void deleteAllByCommentIdIn(Collection<String> commentIds);
}

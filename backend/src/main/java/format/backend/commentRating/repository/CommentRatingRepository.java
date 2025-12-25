package format.backend.commentRating.repository;

import format.backend.commentRating.entity.CommentRatingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRatingRepository extends MongoRepository<CommentRatingEntity, String> {
    Optional<CommentRatingEntity> findByCommentIdAndAuthorId(String commentId, String authorId);

    List<CommentRatingEntity> findAllByCommentIdIn(Collection<String> commentIds);

    void deleteAllByCommentId(String commentId);

    void deleteAllByCommentIdIn(Collection<String> commentIds);
}

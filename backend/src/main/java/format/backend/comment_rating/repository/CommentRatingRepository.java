package format.backend.comment_rating.repository;

import format.backend.comment_rating.entity.CommentRatingEntity;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRatingRepository extends MongoRepository<@NonNull CommentRatingEntity, @NonNull String> {
    Optional<CommentRatingEntity> findByCommentIdAndAuthorId(String commentId, String authorId);

    long deleteAllByCommentId(String commentId);

    long deleteAllByCommentIdIn(List<String> commentIds);
}

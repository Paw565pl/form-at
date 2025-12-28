package format.backend.comment_rating.repository;

import format.backend.comment_rating.entity.CommentRatingEntity;
import java.util.Collection;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRatingRepository extends MongoRepository<@NonNull CommentRatingEntity, @NonNull String> {
    Optional<CommentRatingEntity> findByCommentIdAndAuthorId(String commentId, String authorId);

    void deleteAllByCommentId(String commentId);

    void deleteAllByCommentIdIn(Collection<String> commentIds);
}

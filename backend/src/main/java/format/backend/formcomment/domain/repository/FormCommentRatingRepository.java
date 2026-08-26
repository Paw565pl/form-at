package format.backend.formcomment.domain.repository;

import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormCommentRatingRepository extends MongoRepository<FormCommentRatingEntity, String> {

    Optional<FormCommentRatingEntity> findByCommentIdAndAuthorId(String commentId, String authorId);

    long deleteAllByFormId(String formId);

    long deleteAllByCommentId(String commentId);
}

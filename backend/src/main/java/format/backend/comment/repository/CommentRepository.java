package format.backend.comment.repository;

import format.backend.comment.entity.CommentEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<@NonNull CommentEntity, @NonNull String> {
    Integer countAllByAuthorId(String authorId);

    void deleteAllByFormId(String formId);
}

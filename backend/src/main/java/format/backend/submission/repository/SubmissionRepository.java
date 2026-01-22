package format.backend.submission.repository;

import format.backend.submission.entity.SubmissionEntity;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<@NonNull SubmissionEntity, @NonNull String> {
    Optional<SubmissionEntity> findByIdAndFormId(String id, String formId);

    Optional<SubmissionEntity> findByFormIdAndAuthorId(String formId, String authorId);

    void deleteAllByFormId(String formId);

    Integer countAllByAuthorId(String authorId);
}

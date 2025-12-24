package format.backend.submission.repository;

import format.backend.submission.entity.SubmissionEntity;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<@NonNull SubmissionEntity, @NonNull String> {
    Page<@NonNull SubmissionEntity> findAllByFormId(String formId, Pageable pageable);

    Optional<SubmissionEntity> findByFormIdAndAuthorId(String formId, String authorId);

    void deleteAllByFormId(String formId);

    Integer countAllByAuthorId(String authorId);
}

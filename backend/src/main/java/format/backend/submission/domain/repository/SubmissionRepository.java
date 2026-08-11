package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String>, SubmissionRepositoryCustom {
    Optional<SubmissionEntity> findByIdAndFormId(String id, String formId);

    Optional<SubmissionEntity> findByFormIdAndAuthorId(String formId, String authorId);

    long countAllByAuthorId(String authorId);

    long deleteAllByFormId(String formId);
}

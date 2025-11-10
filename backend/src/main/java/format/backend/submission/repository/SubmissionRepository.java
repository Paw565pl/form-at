package format.backend.submission.repository;

import format.backend.submission.entity.SubmissionEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String> {
    Page<SubmissionEntity> findAllByFormId(String formId, Pageable pageable);

    Optional<SubmissionEntity> findByIdAndFormId(String id, String formId);
}

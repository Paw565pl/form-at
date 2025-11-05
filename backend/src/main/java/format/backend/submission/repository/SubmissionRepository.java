package format.backend.submission.repository;

import format.backend.submission.entity.SubmissionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String> {}

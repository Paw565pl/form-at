package format.backend.storage.repository;

import format.backend.storage.entity.PendingUploadEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingUploadRepository extends MongoRepository<PendingUploadEntity, String> {}

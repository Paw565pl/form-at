package format.backend.storage.repository;

import format.backend.storage.entity.PendingUploadEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingUploadRepository extends MongoRepository<PendingUploadEntity, String> {
    Optional<PendingUploadEntity> findByKey(String key);
}

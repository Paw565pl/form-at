package format.backend.storage.repository;

import format.backend.storage.entity.PendingUploadEntity;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingUploadRepository extends MongoRepository<PendingUploadEntity, String> {
    Slice<PendingUploadEntity> findAllByExpiresAtBefore(Instant now, Pageable pageable);

    Optional<PendingUploadEntity> findByKey(String key);
}

package format.backend.upload.repository;

import format.backend.upload.entity.PendingUploadEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingUploadRepository extends MongoRepository<PendingUploadEntity, String> {
    Slice<PendingUploadEntity> findAllByExpiresAtBefore(Instant now, Pageable pageable);

    List<PendingUploadEntity> findAllByKeyIn(Collection<String> keys);

    int deleteAllByKeyIn(Collection<String> keys);
}

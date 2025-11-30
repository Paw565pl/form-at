package format.backend.upload.repository;

import format.backend.upload.entity.PendingUploadEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PendingUploadRepository extends MongoRepository<@NonNull PendingUploadEntity, @NonNull String> {
    Slice<@NonNull PendingUploadEntity> findAllByExpiresAtBefore(Instant now, Pageable pageable);

    List<PendingUploadEntity> findAllByKeyIn(Collection<String> keys);

    int deleteAllByKeyIn(Collection<String> keys);
}

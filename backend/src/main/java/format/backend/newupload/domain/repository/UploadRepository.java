package format.backend.newupload.domain.repository;

import format.backend.newupload.domain.entity.UploadEntity;
import format.backend.newupload.domain.entity.UploadStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {

    List<UploadEntity> findAllByKeyInAndUserIdAndStatus(Collection<String> keys, String userId, UploadStatus status);

    long countAllByUserIdAndCreatedAtAfter(String userId, Instant createdAtAfter);
}

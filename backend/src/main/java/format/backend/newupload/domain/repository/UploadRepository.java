package format.backend.newupload.domain.repository;

import format.backend.newupload.domain.entity.UploadEntity;
import java.time.Instant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {

    long countAllByUserIdAndCreatedAtAfter(String userId, Instant createdAtAfter);
}

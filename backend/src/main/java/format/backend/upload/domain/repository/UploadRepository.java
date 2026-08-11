package format.backend.upload.domain.repository;

import format.backend.upload.domain.entity.UploadEntity;
import format.backend.upload.domain.entity.UploadStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {

    List<UploadEntity> findAllByTempKeyInAndUserIdAndStatus(
            Collection<String> tempKeys, String userId, UploadStatus status);

    Optional<UploadEntity> findByTempKeyAndStatus(String tempKey, UploadStatus status);

    @Query("{ 'tempKey': { $in: ?0 }, 'status': ?1 }")
    @Update("{ '$set': { 'status': ?2 } }")
    long updateStatusByTempKeyInAndStatus(
            Collection<String> tempKeys, UploadStatus currentStatus, UploadStatus newStatus);

    @Query("{ 'tempKey': ?0, 'status': ?1 }")
    @Update("{ '$set': { 'status': ?2 } }")
    long updateStatusByTempKeyAndStatus(String tempKey, UploadStatus currentStatus, UploadStatus newStatus);

    long countAllByUserIdAndCreatedAtAfter(String userId, Instant createdAtAfter);

    long deleteAllByCreatedAtBefore(Instant createdAtBefore);
}

package format.backend.newupload.domain.repository;

import format.backend.newupload.domain.entity.UploadEntity;
import format.backend.newupload.domain.entity.UploadStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface UploadRepository extends MongoRepository<UploadEntity, String> {

    List<UploadEntity> findAllByKeyInAndUserIdAndStatus(Collection<String> keys, String userId, UploadStatus status);

    long countAllByUserIdAndCreatedAtAfter(String userId, Instant createdAtAfter);

    @Query("{ 'key': { $in: ?0 }, 'userId': ?1, 'status': ?2 }")
    @Update("{ '$set': { 'status': ?3 } }")
    long updateStatusByKeyInAndUserIdAndStatus(
            Collection<String> keys, String userId, UploadStatus currentStatus, UploadStatus newStatus);
}

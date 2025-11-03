package format.backend.storage.job;

import format.backend.storage.properties.MinioProperties;
import format.backend.storage.repository.PendingUploadRepository;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteObject;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class PendingUploadCleanupJob {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final PendingUploadRepository pendingUploadRepository;

    private static final int BATCH_SIZE = 500;

    @Scheduled(cron = "0 0 2 * * *")
    void run() {
        log.info("Started pending upload cleanup job");

        val now = Instant.now();
        val pageable = Pageable.ofSize(BATCH_SIZE);

        var deletedCount = 0;
        var expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);

        while (expiredPendingUploads.hasContent()) {
            val deleteObjects = expiredPendingUploads.stream()
                    .map(upload -> new DeleteObject(upload.getKey()))
                    .toList();

            val removeObjects = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .objects(deleteObjects)
                    .build());
            for (val removeObject : removeObjects) {
                try {
                    val error = removeObject.get();
                    if (error != null)
                        log.error("Error while removing expired pending upload for key {}", error.objectName());
                } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
                    log.error("Caught exception while removing expired pending upload", e);
                }
            }

            pendingUploadRepository.deleteAll(expiredPendingUploads);
            deletedCount += expiredPendingUploads.getNumberOfElements();

            expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);
        }

        log.info("Finished pending upload cleanup job - deleted {} files", deletedCount);
    }
}

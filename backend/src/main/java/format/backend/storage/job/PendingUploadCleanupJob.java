package format.backend.storage.job;

import format.backend.storage.entity.PendingUploadEntity;
import format.backend.storage.repository.PendingUploadRepository;
import format.backend.storage.service.UploadService;
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

    private final PendingUploadRepository pendingUploadRepository;
    private final UploadService uploadService;

    private static final int BATCH_SIZE = 500;

    @Scheduled(cron = "0 0 2 * * *")
    void run() {
        log.info("Started pending upload cleanup job");

        val now = Instant.now();
        val pageable = Pageable.ofSize(BATCH_SIZE);

        var deletedCount = 0;
        var expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);

        while (expiredPendingUploads.hasContent()) {
            val keys = expiredPendingUploads.stream()
                    .map(PendingUploadEntity::getKey)
                    .toList();
            uploadService.deleteAllByKeys(keys);

            pendingUploadRepository.deleteAll(expiredPendingUploads);
            deletedCount += expiredPendingUploads.getNumberOfElements();

            expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);
        }

        log.info("Finished pending upload cleanup job - deleted {} files", deletedCount);
    }
}

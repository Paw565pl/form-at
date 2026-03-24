package format.backend.upload.job;

import format.backend.upload.entity.PendingUploadEntity;
import format.backend.upload.repository.PendingUploadRepository;
import format.backend.upload.service.UploadService;
import java.time.Instant;
import java.util.stream.Collectors;
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

    private static final int BATCH_SIZE = 1_000;

    @Scheduled(cron = "0 0 2 * * *")
    void run() {
        log.info("Started pending upload cleanup job");

        val now = Instant.now();
        val pageable = Pageable.ofSize(BATCH_SIZE);

        var deletedCount = 0L;
        var expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);

        while (expiredPendingUploads.hasContent()) {
            val keys = expiredPendingUploads.stream()
                    .map(PendingUploadEntity::getKey)
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.deleteAllByKeys(keys);

            deletedCount += pendingUploadRepository.deleteAllByKeyIn(keys);
            expiredPendingUploads = pendingUploadRepository.findAllByExpiresAtBefore(now, pageable);
        }

        log.info("Finished pending upload cleanup job - deleted {} files", deletedCount);
    }
}

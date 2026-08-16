package format.backend.newupload.application.delete;

import format.backend.newupload.domain.entity.UploadEntity;
import format.backend.newupload.domain.entity.UploadStatus;
import format.backend.newupload.domain.repository.UploadRepository;
import format.backend.newupload.properties.UploadProperties;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DeleteStalePendingUploadsJob {

    private static final Pageable pageable =
            PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, UploadEntity::getCreatedAt));

    private final UploadProperties uploadProperties;
    private final UploadRepository uploadRepository;
    private final DeleteUploadsHandler deleteUploadsHandler;

    @Scheduled(cron = "0 0 3 * * *")
    void execute() {
        log.debug("Deleting stale uploads.");
        val stalenessWindow = Instant.now().minus(uploadProperties.expiration().pendingUploads());

        var totalDeletedCount = 0L;
        var totalDeleteFailedCount = 0L;
        while (true) {
            val stalePendingUploadKeys = uploadRepository
                    .findAllByStatusAndCreatedAtBefore(UploadStatus.PENDING, stalenessWindow, pageable)
                    .map(UploadEntity::getKey)
                    .toSet();

            val deletedCount = deleteUploadsHandler.handle(stalePendingUploadKeys);
            totalDeletedCount += deletedCount;
            totalDeleteFailedCount += stalePendingUploadKeys.size() - deletedCount;

            if (deletedCount == 0) break;
        }

        log.debug("Deleted {} stale uploads.", totalDeletedCount);
        if (totalDeleteFailedCount > 0) log.warn("Failed to delete {} stale uploads.", totalDeleteFailedCount);
    }
}

package format.backend.upload.application.cleanup;

import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.UploadProperties;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class CleanUploadHistoryJob {

    private final UploadProperties uploadProperties;
    private final UploadRepository uploadRepository;

    @Scheduled(cron = "0 0 3 * * *")
    void execute() {
        val deletedCount = uploadRepository.deleteAllByCreatedAtBefore(
                Instant.now().minus(uploadProperties.retention().staleUploadsWindow()));
        log.debug("Deleted stale uploads. deletedCount={}", deletedCount);
    }
}

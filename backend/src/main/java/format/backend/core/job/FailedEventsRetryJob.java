package format.backend.core.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FailedEventsRetryJob {

    private final FailedEventPublications failedEventPublications;

    @Scheduled(cron = "0 */1 * * * *")
    void retry() {
        log.debug("Retrying failed event publications.");
        failedEventPublications.resubmit(ResubmissionOptions.defaults()
                .withBatchSize(100)
                .withFilter(eventPublication -> eventPublication.getCompletionAttempts() < 5));
        log.debug("Retried failed event publications.");
    }
}

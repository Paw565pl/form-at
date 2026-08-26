package format.backend.submission.application.delete;

import format.backend.form.FormDeletedEvent;
import format.backend.submission.domain.repository.SubmissionRepository;
import format.backend.submission.domain.repository.SubmissionsStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DeleteSubmissionsOnFormDeletedEventListener {

    private final SubmissionRepository submissionRepository;
    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;

    @ApplicationModuleListener
    void on(FormDeletedEvent event) {
        log.debug("Deleting submissions and submissions statistics. event={}", event);

        val deletedSubmissionsCount = submissionRepository.deleteAllByFormId(event.id());
        val deletedSubmissionsStatisticsCount = submissionsStatisticsRepository.deleteAllByFormId(event.id());

        log.debug(
                "Deleted {} submissions and {} submissions statistics.",
                deletedSubmissionsCount,
                deletedSubmissionsStatisticsCount);
    }
}

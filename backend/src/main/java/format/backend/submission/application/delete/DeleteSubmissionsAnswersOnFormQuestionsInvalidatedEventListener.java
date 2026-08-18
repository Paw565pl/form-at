package format.backend.submission.application.delete;

import format.backend.form.FormFacade;
import format.backend.form.FormQuestionsInvalidatedEvent;
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
class DeleteSubmissionsAnswersOnFormQuestionsInvalidatedEventListener {

    private final FormFacade formFacade;

    private final SubmissionRepository submissionRepository;
    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;

    @ApplicationModuleListener
    void on(FormQuestionsInvalidatedEvent event) {
        log.debug("Deleting submissions answers and submissions statistics questions. event={}", event);

        val deletedEmptySubmissionsCount =
                submissionRepository.deleteAnswersByFormIdAndQuestionIdIn(event.id(), event.invalidatedQuestionIds());
        if (deletedEmptySubmissionsCount > 0) {
            formFacade.updateSubmissionsCount(event.id(), -deletedEmptySubmissionsCount);
        }
        submissionsStatisticsRepository.deleteQuestionsByFormIdAndQuestionIdIn(
                event.id(), event.invalidatedQuestionIds());

        log.debug(
                "Deleted submissions answers and submissions statistics questions. Deleted {} empty submissions. event={}",
                deletedEmptySubmissionsCount,
                event);
    }
}

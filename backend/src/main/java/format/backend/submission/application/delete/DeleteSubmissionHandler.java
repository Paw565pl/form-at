package format.backend.submission.application.delete;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.submission.application.shared.SubmissionAccessGuard;
import format.backend.submission.domain.exception.SubmissionNotFoundException;
import format.backend.submission.domain.repository.SubmissionRepository;
import format.backend.submission.domain.repository.SubmissionsStatisticsRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSubmissionHandler {

    private final FormFacade formFacade;

    private final SubmissionRepository submissionRepository;
    private final SubmissionsStatisticsRepository submissionsStatisticsRepository;
    private final SubmissionAccessGuard submissionAccessGuard;

    @Transactional
    public void handle(UserClaims userClaims, String formIdOrSlug, String submissionId) {
        val formView = submissionAccessGuard.verifyAccessAndGetOrThrow(userClaims, formIdOrSlug);
        val submissionEntity = submissionRepository
                .findByIdAndFormId(submissionId, formView.id())
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));

        submissionRepository.deleteById(Objects.requireNonNull(submissionEntity.getId()));
        submissionsStatisticsRepository.update(submissionEntity, -1);
        formFacade.decrementSubmissionsCount(formView.id());
    }
}

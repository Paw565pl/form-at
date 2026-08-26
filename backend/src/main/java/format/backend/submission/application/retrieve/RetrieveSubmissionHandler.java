package format.backend.submission.application.retrieve;

import format.backend.auth.UserClaims;
import format.backend.auth.UserDto;
import format.backend.auth.UserFacade;
import format.backend.submission.application.shared.SubmissionAccessGuard;
import format.backend.submission.application.shared.dto.SubmissionResponseDto;
import format.backend.submission.application.shared.mapper.SubmissionMapper;
import format.backend.submission.domain.exception.SubmissionNotFoundException;
import format.backend.submission.domain.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetrieveSubmissionHandler {

    private final UserFacade userFacade;

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionAccessGuard submissionAccessGuard;

    public SubmissionResponseDto handle(UserClaims userClaims, String formIdOrSlug, String submissionId) {
        val formView = submissionAccessGuard.verifyAccessAndGetOrThrow(userClaims, formIdOrSlug);
        val submissionEntity = submissionRepository
                .findByIdAndFormId(submissionId, formView.id())
                .orElseThrow(() -> new SubmissionNotFoundException(submissionId));
        val authorName = submissionEntity.getAuthorId() != null
                ? userFacade
                        .retrieveById(submissionEntity.getAuthorId())
                        .map(UserDto::username)
                        .orElse(null)
                : null;

        return submissionMapper.toResponseDto(submissionEntity, authorName);
    }
}

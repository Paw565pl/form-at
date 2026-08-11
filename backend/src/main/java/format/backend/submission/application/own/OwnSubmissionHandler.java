package format.backend.submission.application.own;

import format.backend.auth.UserClaims;
import format.backend.auth.UserDto;
import format.backend.auth.UserFacade;
import format.backend.submission.application.shared.SubmissionAccessGuard;
import format.backend.submission.application.shared.dto.SubmissionResponseDto;
import format.backend.submission.application.shared.mapper.SubmissionMapper;
import format.backend.submission.domain.exception.SubmissionNotFoundForUserException;
import format.backend.submission.domain.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnSubmissionHandler {

    private final UserFacade userFacade;

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionAccessGuard submissionAccessGuard;

    public SubmissionResponseDto handle(UserClaims userClaims, String formIdOrSlug) {
        val formView = submissionAccessGuard.verifyOwnAccessAndGetOrThrow(userClaims, formIdOrSlug);
        val submissionEntity = submissionRepository
                .findByFormIdAndAuthorId(formView.id(), userClaims.id())
                .orElseThrow(() -> new SubmissionNotFoundForUserException(formIdOrSlug));
        val authorName = submissionEntity.getAuthorId() != null
                ? userFacade
                        .retrieveById(submissionEntity.getAuthorId())
                        .map(UserDto::username)
                        .orElse(null)
                : null;

        return submissionMapper.toResponseDto(submissionEntity, authorName);
    }
}

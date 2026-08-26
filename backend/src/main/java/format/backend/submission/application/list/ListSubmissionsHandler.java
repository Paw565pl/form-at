package format.backend.submission.application.list;

import format.backend.auth.UserClaims;
import format.backend.submission.application.shared.SubmissionAccessGuard;
import format.backend.submission.application.shared.dto.SubmissionResponseDto;
import format.backend.submission.application.shared.mapper.SubmissionMapper;
import format.backend.submission.domain.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListSubmissionsHandler {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionAccessGuard submissionAccessGuard;

    public Page<SubmissionResponseDto> handle(UserClaims userClaims, String formIdOrSlug, Pageable pageable) {
        val formView = submissionAccessGuard.verifyAccessAndGetOrThrow(userClaims, formIdOrSlug);
        return submissionRepository.findAllByFormId(formView.id(), pageable).map(submissionMapper::toResponseDto);
    }
}

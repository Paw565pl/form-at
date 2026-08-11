package format.backend.submission;

import format.backend.submission.domain.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionFacade {

    private final SubmissionRepository submissionRepository;

    public long count() {
        return submissionRepository.count();
    }

    public long countByAuthorId(String authorId) {
        return submissionRepository.countAllByAuthorId(authorId);
    }
}

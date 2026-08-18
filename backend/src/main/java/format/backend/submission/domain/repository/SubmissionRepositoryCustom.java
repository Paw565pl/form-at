package format.backend.submission.domain.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface SubmissionRepositoryCustom {

    Page<SubmissionListProjection> findAllByFormId(String formId, Pageable pageable);

    /// returns deleted empty submissions count
    long deleteAnswersByFormIdAndQuestionIdIn(String formId, Collection<String> questionIds);
}

package format.backend.submission.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface SubmissionRepositoryCustom {

    Page<SubmissionListProjection> findAllByFormId(String formId, Pageable pageable);
}

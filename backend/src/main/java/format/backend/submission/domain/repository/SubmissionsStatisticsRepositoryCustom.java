package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionEntity;

interface SubmissionsStatisticsRepositoryCustom {

    void update(SubmissionEntity submissionEntity, int delta);
}

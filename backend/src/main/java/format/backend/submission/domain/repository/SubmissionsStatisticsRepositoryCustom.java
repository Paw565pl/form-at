package format.backend.submission.domain.repository;

import format.backend.submission.domain.entity.SubmissionEntity;
import java.util.Collection;

interface SubmissionsStatisticsRepositoryCustom {

    void update(SubmissionEntity submissionEntity, int delta);

    void deleteQuestionsByFormIdAndQuestionIdIn(String formId, Collection<String> questionIds);
}

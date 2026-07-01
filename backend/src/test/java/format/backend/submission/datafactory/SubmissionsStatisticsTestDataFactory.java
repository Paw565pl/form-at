package format.backend.submission.datafactory;

import format.backend.submission.entity.SubmissionsStatisticsEntity;
import java.util.Map;

public abstract class SubmissionsStatisticsTestDataFactory {

    public static SubmissionsStatisticsEntity create(
            String formId, Map<String, SubmissionsStatisticsEntity.Statistics> questions) {
        var submissionsStatistics = new SubmissionsStatisticsEntity(formId);
        submissionsStatistics.getQuestions().putAll(questions);

        return submissionsStatistics;
    }
}

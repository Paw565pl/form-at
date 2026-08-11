package format.backend.submission.application.statistics.retrieve;

import java.util.Map;

public record RetrieveSubmissionsStatisticsResponseDto(long submissionsCount, Map<String, Statistics> questions) {
    public record Statistics(Map<String, Long> answers) {}
}

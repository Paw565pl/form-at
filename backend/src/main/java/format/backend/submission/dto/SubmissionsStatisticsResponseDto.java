package format.backend.submission.dto;

import java.util.Map;

public record SubmissionsStatisticsResponseDto(Long submissionsCount, Map<String, Statistics> questions) {
    public record Statistics(Map<String, Long> answers) {}
}

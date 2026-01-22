package format.backend.submission.dto;

import java.util.List;

public record SubmissionStatisticsResponseDto(
        String questionId, List<SubmissionAnswersStatisticsResponseDto> submissionStatistics) {}

package format.backend.statistics.application.retrieve;

import lombok.Builder;

@Builder
public record RetrieveStatisticsResponseDto(long usersCount, long formsCount, long submissionsCount) {}

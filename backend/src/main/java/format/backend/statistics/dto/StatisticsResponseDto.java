package format.backend.statistics.dto;

import org.jspecify.annotations.NonNull;

public record StatisticsResponseDto(
        @NonNull Long usersCount,
        @NonNull Long formsCount,
        @NonNull Long submissionsCount) {}

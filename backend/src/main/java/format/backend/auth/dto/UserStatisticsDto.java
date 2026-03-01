package format.backend.auth.dto;

import org.jspecify.annotations.NonNull;

public record UserStatisticsDto(
        @NonNull Long formsCount,
        @NonNull Long submissionsCount,
        @NonNull Long commentsCount) {}

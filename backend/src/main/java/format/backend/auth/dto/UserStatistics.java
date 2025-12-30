package format.backend.auth.dto;

import org.jspecify.annotations.NonNull;

public record UserStatistics(
        @NonNull Integer formsCount,
        @NonNull Integer submissionsCount,
        @NonNull Integer commentsCount) {}

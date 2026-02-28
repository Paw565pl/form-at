package format.backend.auth.dto;

import org.jspecify.annotations.NonNull;

public record UserProfileResponseDto(
        @NonNull String id,
        @NonNull String username,
        @NonNull UserStatisticsDto statistics) {}

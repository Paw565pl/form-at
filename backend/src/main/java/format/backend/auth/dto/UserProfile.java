package format.backend.auth.dto;

import org.jspecify.annotations.NonNull;

public record UserProfile(
        @NonNull String id,
        @NonNull String username,
        @NonNull UserStatistics statistics) {}

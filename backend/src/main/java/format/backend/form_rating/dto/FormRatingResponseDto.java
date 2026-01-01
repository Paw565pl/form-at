package format.backend.form_rating.dto;

import org.jspecify.annotations.NonNull;
import java.time.Instant;

public record FormRatingResponseDto(
        @NonNull String id,

        @NonNull Integer rating,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

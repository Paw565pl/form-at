package format.backend.form_rating.dto;

import java.time.Instant;
import org.jspecify.annotations.NonNull;

public record FormRatingResponseDto(
        @NonNull String id,

        @NonNull Integer rating,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

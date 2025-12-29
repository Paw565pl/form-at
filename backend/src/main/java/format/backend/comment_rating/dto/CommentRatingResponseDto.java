package format.backend.comment_rating.dto;

import format.backend.comment_rating.entity.RatingType;
import java.time.Instant;
import org.jspecify.annotations.NonNull;

public record CommentRatingResponseDto(
        @NonNull String id,

        @NonNull RatingType type,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

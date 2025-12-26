package format.backend.commentRating.dto;

import format.backend.commentRating.entity.RatingType;
import java.time.Instant;
import org.jspecify.annotations.NonNull;

public record CommentRatingResponseDto(
        @NonNull String id,

        @NonNull String commentId,

        @NonNull RatingType type,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

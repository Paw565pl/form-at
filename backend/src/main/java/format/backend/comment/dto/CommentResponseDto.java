package format.backend.comment.dto;

import format.backend.comment_rating.entity.RatingType;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CommentResponseDto(
        @NonNull String id,

        @Nullable String authorName,

        @NonNull String content,

        @NonNull Long ratingScore,

        @Nullable RatingType userRating,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

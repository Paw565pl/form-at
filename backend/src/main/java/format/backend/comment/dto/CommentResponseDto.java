package format.backend.comment.dto;

import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CommentResponseDto(
        @NonNull String id,

        @Nullable String authorName,

        @NonNull String content,

        @NonNull Long ratingScore,

        @NonNull Integer userRating,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

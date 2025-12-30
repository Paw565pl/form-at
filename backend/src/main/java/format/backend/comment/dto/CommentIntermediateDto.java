package format.backend.comment.dto;

import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record CommentIntermediateDto(
        @NonNull String id,

        @Nullable String authorName,

        @NonNull String content,

        @NonNull Long ratingScore,

        @Nullable Integer userRating,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

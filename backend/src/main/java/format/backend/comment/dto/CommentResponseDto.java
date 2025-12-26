package format.backend.comment.dto;

import java.time.Instant;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record CommentResponseDto(
        @NonNull String id,

        @Nullable String authorName,

        @NonNull String content,

        @NonNull Long ratingScore,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

package format.backend.comment.dto;

import java.time.Instant;
import org.springframework.lang.NonNull;

public record CommentResponseDto(
        @NonNull String id,

        @NonNull String authorName,

        @NonNull String content,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

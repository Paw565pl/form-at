package format.backend.comment.dto;

import java.time.Instant;
import org.springframework.lang.NonNull;

public class CommentResponseDto {
    @NonNull private String id;

    @NonNull private String authorName;

    @NonNull private String formId;

    @NonNull private String content;

    @NonNull private Instant createdAt;

    @NonNull private Instant updatedAt;
}

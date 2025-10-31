package format.backend.comment.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    @NonNull private String id;

    @NonNull private String authorName;

    @NonNull private String formId;

    @NonNull private String content;

    @NonNull private Instant createdAt;

    @NonNull private Instant updatedAt;
}

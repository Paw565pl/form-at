package format.backend.comment.dto;

import java.time.Instant;
import org.springframework.lang.NonNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    @NonNull private String id;
    @NonNull private String authorId;
    @NonNull private String content;
    @NonNull private Instant createdAt;
    @NonNull private Instant updatedAt;
}

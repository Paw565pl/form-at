package format.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

public record CommentRequestDto(
        @NotBlank(message = "Comment content cannot be blank") @Size(min = 3, max = 500, message = "Comment must be between 3 and 500 characters long") @NonNull String content) {}

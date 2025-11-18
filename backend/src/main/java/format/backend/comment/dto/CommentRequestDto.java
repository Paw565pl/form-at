package format.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

public record CommentRequestDto(
        @Size(min = 3, max = 500, message = "Content must be between 3 and 500 characters long") @NotBlank(message = "Content cannot be blank") @NonNull String content) {}

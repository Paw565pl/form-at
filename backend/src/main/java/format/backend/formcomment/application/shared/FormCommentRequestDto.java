package format.backend.formcomment.application.shared;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormCommentRequestDto(
        @Size(min = 3, max = 500, message = "Content must be between 3 and 500 characters long") @NotBlank(message = "Content cannot be blank") String content) {}

package format.backend.form.application.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnswerRequestDto(
        @Size(min = 3, max = 200, message = "Content must be between 3 and 200 characters long") @NotBlank(message = "Content cannot be blank") String content,

        @NotNull(message = "IsCorrect cannot be null") Boolean isCorrect) {}

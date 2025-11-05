package format.backend.form.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

public record AnswerRequestDto(
        @Size(min = 3, max = 200, message = "Content must be between 3 and 200 characters long") @NotBlank(message = "Content cannot be blank") @NonNull String content,

        @NotNull(message = "IsCorrect cannot be null") @NonNull Boolean isCorrect) {}

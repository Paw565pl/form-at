package format.backend.form.dto;

import format.backend.form.entity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record QuestionRequestDto(
        @Size(min = 3, max = 200, message = "Content must be between 3 and 200 characters long") @NotBlank(message = "Content cannot be blank") @NonNull String content,

        @NotNull(message = "Type cannot be null") @NonNull QuestionType type,

        @Size(min = 10, max = 200, message = "Image key must be between 10 and 200 characters long") @Nullable String imageKey,

        @NotNull(message = "IsRequired cannot be null") @NonNull Boolean isRequired,

        @Size(min = 0, max = 6, message = "Answers must contain between 0 and 6 items") @NotNull(message = "Answers cannot be null") @NonNull List<AnswerRequestDto> answers) {}

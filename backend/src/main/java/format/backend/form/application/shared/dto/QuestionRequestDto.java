package format.backend.form.application.shared.dto;

import format.backend.form.application.shared.validator.ValidQuestion;
import format.backend.form.domain.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ValidQuestion
public record QuestionRequestDto(
        @Size(min = 3, max = 200, message = "Content must be between 3 and 200 characters long") @NotBlank(message = "Content cannot be blank") String content,

        @NotNull(message = "Type cannot be null") QuestionType type,

        @Size(min = 50, max = 300, message = "Image key must be between 50 and 300 characters long") @Nullable String imageKey,

        @NotNull(message = "IsRequired cannot be null") Boolean isRequired,

        @Size(min = 0, max = 6, message = "Answers must contain between 0 and 6 items") @NotNull(message = "Answers cannot be null") List<@Valid AnswerRequestDto> answers) {}

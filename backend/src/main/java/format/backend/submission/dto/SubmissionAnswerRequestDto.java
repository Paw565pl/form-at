package format.backend.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record SubmissionAnswerRequestDto(
        @NotBlank(message = "Question id cannot be blank") @NonNull String questionId, // TODO: add valid object id validator

        @NotNull(message = "ChosenAnswerIds cannot be null") @NonNull Set<@NotBlank(message = "ChosenAnswerId cannot be blank") String> chosenAnswerIds,

        @Size(min = 10, max = 1000, message = "OpenAnswer must be between 1 and 1000 characters long") @Nullable String openAnswer) {}

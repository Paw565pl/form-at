package format.backend.submission.application.shared.dto;

import format.backend.core.validator.ValidObjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public record SubmissionAnswerRequestDto(
        @ValidObjectId(message = "QuestionId must be a valid ObjectId")
        @NotBlank(message = "Question id cannot be blank") String questionId,

        @NotNull(message = "ChosenAnswerIds cannot be null") Set<
                        @ValidObjectId(message = "ChosenAnswerId must be a valid ObjectId")
                        @NotBlank(message = "ChosenAnswerId cannot be blank") String>
                chosenAnswerIds,

        @Size(min = 10, max = 1000, message = "OpenAnswer must be between 10 and 1000 characters long") @Nullable String openAnswer) {}

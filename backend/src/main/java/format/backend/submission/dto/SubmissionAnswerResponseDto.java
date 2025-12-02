package format.backend.submission.dto;

import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record SubmissionAnswerResponseDto(
        @NonNull String questionId,

        @NonNull Set<@NonNull String> chosenAnswerIds,

        @Nullable String openAnswer) {}

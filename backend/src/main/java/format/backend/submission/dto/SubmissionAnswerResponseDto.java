package format.backend.submission.dto;

import java.util.Set;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record SubmissionAnswerResponseDto(
        @NonNull String questionId,

        @NonNull Set<String> chosenAnswerIds,

        @Nullable String openAnswer) {}

package format.backend.submission.application.shared.dto;

import java.util.Set;
import org.jspecify.annotations.Nullable;

public record SubmissionAnswerResponseDto(
        String questionId,

        Set<String> chosenAnswerIds,

        @Nullable String openAnswer) {}

package format.backend.submission.dto;

import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record SubmissionResponseDto(
        @NonNull String id,

        @Nullable String authorName,

        @NonNull List<@NonNull SubmissionAnswerResponseDto> answers,

        @NonNull Instant createdAt) {}

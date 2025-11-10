package format.backend.submission.dto;

import java.time.Instant;
import java.util.List;
import org.springframework.lang.NonNull;

public record SubmissionResponseDto(
        @NonNull String id,

        @NonNull List<SubmissionAnswerResponseDto> answers,

        @NonNull Instant createdAt) {}

package format.backend.submission.application.shared.dto;

import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record SubmissionResponseDto(
        String id, @Nullable String authorName, List<SubmissionAnswerResponseDto> answers, Instant createdAt) {}

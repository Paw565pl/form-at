package format.backend.submission.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.lang.NonNull;

public record SubmissionRequestDto(
        @NotEmpty(message = "Answers cannot be empty") @NonNull List<SubmissionAnswerRequestDto> answers) {}

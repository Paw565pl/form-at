package format.backend.submission.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.lang.NonNull;

public record SubmissionRequestDto(
        @NotEmpty(message = "Answers cannot be empty") @Valid @NonNull List<SubmissionAnswerRequestDto> answers) {}

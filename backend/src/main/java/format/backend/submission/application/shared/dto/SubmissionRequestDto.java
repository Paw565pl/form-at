package format.backend.submission.application.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SubmissionRequestDto(
        @Size(min = 3, max = 100, message = "Answers must contain between 3 and 100 items") @NotEmpty(message = "Answers cannot be empty") List<@Valid SubmissionAnswerRequestDto> answers) {}

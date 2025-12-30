package format.backend.upload.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.jspecify.annotations.NonNull;

public record BatchUploadRequestDto(
        @Size(min = 1, max = 200, message = "Files must contain between 1 and 200 elements") @NotEmpty(message = "Files cannot be empty") @Valid @NonNull List<@NonNull UploadRequestDto> files) {}

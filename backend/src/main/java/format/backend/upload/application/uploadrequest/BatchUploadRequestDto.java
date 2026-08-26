package format.backend.upload.application.uploadrequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BatchUploadRequestDto(
        @Size(min = 1, max = 110, message = "Files must contain between 1 and 110 elements") @NotEmpty(message = "Files cannot be empty") List<@Valid UploadRequestDto> files) {}

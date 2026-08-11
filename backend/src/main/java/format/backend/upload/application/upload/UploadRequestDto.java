package format.backend.upload.application.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UploadRequestDto(
        @ValidImageExtension
        @Size(min = 5, max = 200, message = "Filename must be between 5 and 200 characters long") @NotBlank(message = "Filename cannot be blank") String filename) {}

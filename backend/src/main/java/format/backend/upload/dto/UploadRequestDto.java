package format.backend.upload.dto;

import format.backend.upload.validator.ValidImageExtension;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

public record UploadRequestDto(
        @ValidImageExtension
        @Size(min = 3, max = 200, message = "Filename must be between 3 and 200 characters long") @NotBlank(message = "Filename cannot be blank") @NonNull String filename) {}

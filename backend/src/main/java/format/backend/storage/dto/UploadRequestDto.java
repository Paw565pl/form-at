package format.backend.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.NonNull;

public record UploadRequestDto(
        @Size(min = 3, max = 200, message = "FileName must be between 3 and 200 characters long") @NotBlank(message = "FileName cannot be blank") @NonNull String fileName) {}

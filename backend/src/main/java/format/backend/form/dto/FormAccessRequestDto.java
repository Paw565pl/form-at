package format.backend.form.dto;

import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record FormAccessRequestDto(
        @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters long") @Nullable String password) {}

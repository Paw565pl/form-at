package format.backend.form.application.access;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccessPrivateFormRequestDto(
        @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters long") @NotBlank(message = "Password cannot be blank") String password) {}

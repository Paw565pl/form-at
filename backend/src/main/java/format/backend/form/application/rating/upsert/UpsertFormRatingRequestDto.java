package format.backend.form.application.rating.upsert;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpsertFormRatingRequestDto(
        @Min(value = 1, message = "Value must be at least 1") @Max(value = 5, message = "Value must be at most 5") @NotNull(message = "Value cannot be null") Integer value) {}

package format.backend.form_rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

public record FormRatingRequestDto(
        @NotNull(message = "RatingValue cannot be null") @Min(value = 1, message = "RatingValue must be at least 1") @Max(value = 5, message = "RatingValue must be at most 5") @NonNull Integer ratingValue) {}

package format.backend.form.dto;

import format.backend.core.validator.DurationRange;
import format.backend.form.entity.FormShuffleVariant;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record FormRequestDto(
        @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters long") @NotBlank(message = "Name cannot be blank") @NonNull String name,

        @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters long") @Nullable String description,

        @NotNull(message = "Language cannot be null") @NonNull Language language,

        @NotNull(message = "Status cannot be null") @NonNull FormStatus status,

        @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters long") @Nullable String password,

        @Nullable FormShuffleVariant shuffleVariant,

        @Size(min = 3, max = 500, message = "ThanksMessage must be between 3 and 500 characters long") @Nullable String thanksMessage,

        @DurationRange(min = "PT1M", max = "PT2H", message = "EstimatedDuration must be between 1 minute and 2 hours")
        @NotNull(message = "EstimatedDuration cannot be null") @NonNull Duration estimatedDuration,

        @Size(min = 10, max = 200, message = "Thumbnail key must be between 10 and 200 characters long") @Nullable String thumbnailKey,

        @NotNull(message = "AllowsQuestionsPreview cannot be null") @NonNull Boolean allowsQuestionsPreview,

        @NotNull(message = "AllowsGuestSubmissions cannot be null") @NonNull Boolean allowsGuestSubmissions,

        @NotNull(message = "SaveSubmissions cannot be null") @NonNull Boolean saveSubmissions,

        @Size(min = 3, max = 100, message = "Questions must contain between 3 and 100 items") @NotEmpty(message = "Questions cannot be empty") @Valid @NonNull List<QuestionRequestDto> questions) {}

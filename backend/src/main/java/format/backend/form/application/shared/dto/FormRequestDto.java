package format.backend.form.application.shared.dto;

import format.backend.core.validator.DurationRange;
import format.backend.form.application.shared.validator.ValidForm;
import format.backend.form.domain.entity.FormLanguage;
import format.backend.form.domain.entity.FormShuffleVariant;
import format.backend.form.domain.entity.FormStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ValidForm
public record FormRequestDto(
        @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters long") @NotBlank(message = "Name cannot be blank") String name,

        @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters long") @Nullable String description,

        @NotNull(message = "Language cannot be null") FormLanguage language,

        @NotNull(message = "Status cannot be null") FormStatus status,

        @Size(min = 8, max = 200, message = "Password must be between 8 and 200 characters long") @Nullable String password,

        @Nullable FormShuffleVariant shuffleVariant,

        @Size(min = 3, max = 500, message = "ThanksMessage must be between 3 and 500 characters long") @Nullable String thanksMessage,

        @DurationRange(min = "PT1M", max = "PT2H", message = "EstimatedDuration must be between 1 minute and 2 hours")
        @NotNull(message = "EstimatedDuration cannot be null") Duration estimatedDuration,

        @Size(min = 50, max = 300, message = "Thumbnail key must be between 50 and 300 characters long") @Nullable String thumbnailKey,

        @NotNull(message = "AllowsQuestionsPreview cannot be null") Boolean allowsQuestionsPreview,

        @NotNull(message = "AllowsGuestSubmissions cannot be null") Boolean allowsGuestSubmissions,

        @NotNull(message = "SaveSubmissions cannot be null") Boolean saveSubmissions,

        @NotNull(message = "ShowAnswersFeedback cannot be null") Boolean showAnswersFeedback,

        @Size(min = 3, max = 100, message = "Questions must contain between 3 and 100 items") @NotEmpty(message = "Questions cannot be empty") List<@Valid QuestionRequestDto> questions) {}

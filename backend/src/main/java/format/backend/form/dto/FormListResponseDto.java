package format.backend.form.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record FormListResponseDto(
        @NonNull String id,

        @NonNull String name,

        @NonNull String slug,

        @Nullable String description,

        @NonNull Language language,

        @NonNull FormStatus status,

        @NonNull Duration estimatedDuration,

        @Nullable String thumbnail,

        @NonNull Boolean allowsQuestionsPreview,

        @NonNull Boolean allowsGuestSubmissions,

        @NonNull Boolean saveSubmissions,

        @NonNull Boolean showAnswersFeedback,

        @NonNull Integer questionsCount,

        @NonNull Long submissionsCount,

        @NonNull Long ratingsCount,

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "0.0") @Nullable Double ratingAvg,

        @Nullable String authorName,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

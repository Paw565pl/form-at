package format.backend.form.dto;

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

        @Nullable String authorName,

        @NonNull Long submissionsCount,

        @NonNull Integer questionsCount,

        @NonNull Long ratingsCount,

        @NonNull Double ratingAvg,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

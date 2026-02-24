package format.backend.form.entity;

import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record FormListAggregationResult(
        @NonNull String id,

        @NonNull String name,

        @NonNull String slug,

        @Nullable String description,

        @NonNull Language language,

        @NonNull FormStatus status,

        @NonNull Duration estimatedDuration,

        @Nullable String thumbnailKey,

        @NonNull Boolean allowsQuestionsPreview,

        @NonNull Boolean allowsGuestSubmissions,

        @NonNull Boolean saveSubmissions,

        @NonNull Boolean showAnswersFeedback,

        @Nullable String authorName,

        @NonNull Long submissionsCount,

        @NonNull Integer questionsCount,

        @NonNull Long ratingsCount,

        @Nullable Double ratingAvg,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

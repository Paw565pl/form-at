package format.backend.form.entity;

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

        @NonNull Integer estimatedDurationSeconds,

        @Nullable String thumbnailKey,

        @NonNull Boolean allowsQuestionsPreview,

        @NonNull Boolean allowsGuestSubmissions,

        @NonNull Boolean saveSubmissions,

        @NonNull Boolean showAnswersFeedback,

        @NonNull Integer questionsCount,

        @NonNull Long submissionsCount,

        @NonNull Long ratingsCount,

        @NonNull Long ratingsSum,

        @Nullable String authorName,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {
    public @Nullable Double getRatingAvg() {
        return ratingsCount == 0 ? null : (double) ratingsSum / ratingsCount;
    }
}

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

        @Nullable String authorName,

        @NonNull Long submissionsCount,

        @NonNull Integer questionsCount,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

package format.backend.form.dto;

import format.backend.form.entity.FormStatus;
import java.time.Duration;
import java.time.Instant;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record FormListResponseDto(
        @NonNull String id,

        @NonNull String name,

        @NonNull String slug,

        @Nullable String description,

        @NonNull FormStatus status,

        @NonNull Duration estimatedDuration,

        @Nullable String thumbnail,

        @NonNull Boolean allowsQuestionsPreview,

        @NonNull Boolean allowsGuestSubmissions,

        @NonNull Boolean saveSubmissions,

        @NonNull Long submissionsCount,

        @NonNull Integer questionsCount,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

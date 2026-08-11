package format.backend.form.domain.repository;

import format.backend.form.domain.entity.FormLanguage;
import format.backend.form.domain.entity.FormStatus;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record FormListProjection(
        String id,

        String name,

        String slug,

        @Nullable String description,

        FormLanguage language,

        FormStatus status,

        long estimatedDurationSeconds,

        @Nullable String thumbnailKey,

        Boolean allowsQuestionsPreview,

        Boolean allowsGuestSubmissions,

        Boolean saveSubmissions,

        Boolean showAnswersFeedback,

        int questionsCount,

        long submissionsCount,

        long ratingsCount,

        long ratingsSum,

        @Nullable String authorName,

        Instant createdAt,

        Instant updatedAt) {}

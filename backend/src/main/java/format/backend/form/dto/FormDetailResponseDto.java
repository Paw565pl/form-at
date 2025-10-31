package format.backend.form.dto;

import format.backend.form.entity.FormShuffleVariant;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record FormDetailResponseDto(
        @NonNull String id,

        @NonNull String name,

        @NonNull String slug,

        @Nullable String description,

        @NonNull Language language,

        @NonNull FormStatus status,

        @Nullable FormShuffleVariant shuffleVariant,

        @Nullable String thanksMessage,

        @NonNull Duration estimatedDuration,

        @Nullable String thumbnail,

        @NonNull Boolean allowsQuestionsPreview,

        @NonNull Boolean allowsGuestSubmissions,

        @NonNull Boolean saveSubmissions,

        @NonNull Long submissionsCount,

        @NonNull List<QuestionResponseDto> questions,

        @NonNull Instant createdAt,

        @NonNull Instant updatedAt) {}

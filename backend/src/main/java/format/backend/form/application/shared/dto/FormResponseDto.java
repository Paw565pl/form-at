package format.backend.form.application.shared.dto;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER_FLOAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import format.backend.form.domain.entity.FormLanguage;
import format.backend.form.domain.entity.FormShuffleVariant;
import format.backend.form.domain.entity.FormStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record FormResponseDto(
        String id,

        String name,

        String slug,

        @Nullable String description,

        FormLanguage language,

        FormStatus status,

        @Nullable FormShuffleVariant shuffleVariant,

        @Nullable String thanksMessage,

        Duration estimatedDuration,

        @Nullable String thumbnail,

        boolean allowsQuestionsPreview,

        boolean allowsGuestSubmissions,

        boolean saveSubmissions,

        boolean showAnswersFeedback,

        List<QuestionResponseDto> questions,

        long submissionsCount,

        long ratingsCount,

        @JsonFormat(shape = NUMBER_FLOAT, pattern = "0.0") @Nullable Double ratingAvg,

        @Nullable Integer userRating,

        @Nullable String authorName,

        Instant createdAt,

        Instant updatedAt) {}

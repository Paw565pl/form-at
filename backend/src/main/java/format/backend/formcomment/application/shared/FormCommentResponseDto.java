package format.backend.formcomment.application.shared;

import format.backend.formcomment.domain.entity.FormCommentRatingType;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record FormCommentResponseDto(
        String id,

        @Nullable String authorName,

        String content,

        long ratingScore,

        @Nullable FormCommentRatingType userRating,

        Instant createdAt,

        Instant updatedAt) {}

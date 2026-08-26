package format.backend.formcomment.application.rating.upsert;

import format.backend.formcomment.domain.entity.FormCommentRatingType;
import java.time.Instant;

public record UpsertFormCommentRatingResponseDto(
        String id, FormCommentRatingType type, Instant createdAt, Instant updatedAt) {}

package format.backend.formcomment.application.rating.upsert;

import format.backend.formcomment.domain.entity.FormCommentRatingType;
import jakarta.validation.constraints.NotNull;

public record UpsertFormCommentRatingResponseDto(
        @NotNull(message = "Type cannot be null") FormCommentRatingType type) {}

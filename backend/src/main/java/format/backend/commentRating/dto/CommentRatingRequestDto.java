package format.backend.commentRating.dto;

import format.backend.commentRating.entity.RatingType;
import jakarta.validation.constraints.NotNull;

public record CommentRatingRequestDto(
        @NotNull RatingType type) {}

package format.backend.commentRating.dto;

import format.backend.commentRating.entity.RatingType;
import org.jspecify.annotations.NonNull;

public record CommentRatingRequestDto(
        @NonNull RatingType type) {}

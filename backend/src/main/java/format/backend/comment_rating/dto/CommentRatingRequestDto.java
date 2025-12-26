package format.backend.comment_rating.dto;

import format.backend.comment_rating.entity.RatingType;
import org.jspecify.annotations.NonNull;

public record CommentRatingRequestDto(
        @NonNull RatingType type) {}

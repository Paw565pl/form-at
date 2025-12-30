package format.backend.comment_rating.dto;

import format.backend.comment_rating.entity.RatingType;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

public record CommentRatingRequestDto(@NotNull @NonNull RatingType type) {}

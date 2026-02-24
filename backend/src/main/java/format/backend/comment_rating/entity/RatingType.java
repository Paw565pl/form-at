package format.backend.comment_rating.entity;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RatingType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;

    public static Optional<RatingType> fromValue(int value) {
        for (var ratingType : values()) {
            if (ratingType.getValue() == value) {
                return Optional.of(ratingType);
            }
        }

        return Optional.empty();
    }
}

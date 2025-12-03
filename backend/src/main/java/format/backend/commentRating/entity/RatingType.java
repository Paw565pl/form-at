package format.backend.commentRating.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RatingType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;

    public static RatingType fromValue(int value) {
        for (RatingType t : values()) {
            if (t.value == value) return t;
        }
        throw new IllegalArgumentException("Unknown rating value: " + value);
    }
}

package format.backend.comment_rating.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RatingType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;

    public static RatingType fromValue(int value) {
        return switch (value) {
            case 1 -> UPVOTE;
            case -1 -> DOWNVOTE;
            default -> throw new IllegalStateException("Invalid rating value: " + value);
        };
    }
}

package format.backend.comment_rating.entity;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RatingType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;

    public static Optional<RatingType> fromValue(int value) {
        return switch (value) {
            case 1 -> Optional.of(UPVOTE);
            case -1 -> Optional.of(DOWNVOTE);
            default -> Optional.empty();
        };
    }
}

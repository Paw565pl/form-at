package format.backend.comment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RatingType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;
}

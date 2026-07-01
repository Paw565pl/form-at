package format.backend.comment_rating.datafactory;

import format.backend.comment_rating.entity.CommentRatingEntity;

public abstract class CommentRatingTestDataFactory {

    public static CommentRatingEntity create(String commentId, String authorId, boolean isPositive) {
        var commentRating = new CommentRatingEntity(commentId);
        commentRating.setAuthorId(authorId);
        commentRating.setType(isPositive ? 1 : 0);

        return commentRating;
    }
}

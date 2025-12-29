package format.backend.comment_rating.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public class CommentNotRatedByUserException extends ApplicationException {
    public CommentNotRatedByUserException(@NonNull String commentId) {
        super(HttpStatus.NOT_FOUND, String.format("Comment wih id '%s' has not been rated by the user", commentId));
    }
}

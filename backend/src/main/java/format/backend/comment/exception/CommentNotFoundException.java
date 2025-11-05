package format.backend.comment.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class CommentNotFoundException extends ApplicationException {
    public CommentNotFoundException(String id) {
        super(HttpStatus.BAD_REQUEST, String.format("Comment with id: %s was not found", id));
    }
}

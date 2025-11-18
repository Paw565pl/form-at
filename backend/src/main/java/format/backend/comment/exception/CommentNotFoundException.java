package format.backend.comment.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class CommentNotFoundException extends ApplicationException {

    public CommentNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, String.format("Comment with id: '%s' was not found", id));
    }
}

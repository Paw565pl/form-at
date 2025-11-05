package format.backend.comment.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(String id) {
        super(HttpStatus.BAD_REQUEST, String.format("User with id: %s was not found", id));
    }
}

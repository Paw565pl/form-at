package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.form.entity.FormStatus;
import org.springframework.http.HttpStatus;

public class BlankPasswordException extends ApplicationException {

    public BlankPasswordException() {
        super(
                HttpStatus.BAD_REQUEST,
                String.format("Password cannot be blank for form with status '%s'", FormStatus.PRIVATE));
    }
}

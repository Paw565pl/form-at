package format.backend.form.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class InvalidFormPasswordException extends ApplicationException {

    public InvalidFormPasswordException() {
        super("Incorrect password", ApplicationExceptionType.FORBIDDEN, "INVALID_FORM_PASSWORD");
    }
}

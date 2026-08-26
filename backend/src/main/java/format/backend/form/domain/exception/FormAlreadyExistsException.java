package format.backend.form.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class FormAlreadyExistsException extends ApplicationException {

    public FormAlreadyExistsException(String name) {
        super(
                "Form with name '%s' already exists".formatted(name),
                ApplicationExceptionType.CONFLICT,
                "FORM_ALREADY_EXISTS");
    }
}

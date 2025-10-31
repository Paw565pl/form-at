package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FormAlreadyExistsException extends ApplicationException {

    public FormAlreadyExistsException(String name) {
        super(HttpStatus.CONFLICT, String.format("Form with name '%s' already exists", name));
    }
}

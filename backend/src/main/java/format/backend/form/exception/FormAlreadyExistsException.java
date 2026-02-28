package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class FormAlreadyExistsException extends ApplicationException {

    public FormAlreadyExistsException(@NonNull String name) {
        super(HttpStatus.CONFLICT, "FORM_ALREADY_EXISTS", String.format("Form with name '%s' already exists", name));
    }
}

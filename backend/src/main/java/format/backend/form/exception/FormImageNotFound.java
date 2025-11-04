package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class FormImageNotFound extends ApplicationException {

    public FormImageNotFound(String key) {
        super(HttpStatus.BAD_REQUEST, String.format("Form image with key %s was not found in storage", key));
    }
}

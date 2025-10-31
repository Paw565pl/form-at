package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FormNotFoundException extends ApplicationException {

    public FormNotFoundException(String idOrSlug) {
        super(HttpStatus.NOT_FOUND, String.format("Form with id or slug '%s' not found", idOrSlug));
    }
}

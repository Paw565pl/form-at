package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class FormNotFoundException extends ApplicationException {

    public FormNotFoundException(@NonNull String idOrSlug) {
        super(HttpStatus.NOT_FOUND, String.format("Form with id or slug '%s' not found", idOrSlug));
    }
}

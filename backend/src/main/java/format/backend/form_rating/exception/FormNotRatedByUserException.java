package format.backend.form_rating.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class FormNotRatedByUserException extends ApplicationException {
    public FormNotRatedByUserException(@NonNull String formIdOrSlug) {
        super(
                HttpStatus.NOT_FOUND,
                String.format("Form wih id or slug '%s' has not been rated by the user", formIdOrSlug));
    }
}

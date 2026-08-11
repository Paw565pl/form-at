package format.backend.form.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class FormNotRatedByUserException extends ApplicationException {

    public FormNotRatedByUserException(String formIdOrSlug) {
        super(
                "Form wih id or slug '%s' has not been rated by the user".formatted(formIdOrSlug),
                ApplicationExceptionType.NOT_FOUND,
                "FORM_NOT_RATED_BY_USER");
    }
}

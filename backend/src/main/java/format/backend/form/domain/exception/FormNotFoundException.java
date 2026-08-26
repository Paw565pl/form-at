package format.backend.form.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class FormNotFoundException extends ApplicationException {

    public FormNotFoundException(String idOrSlug) {
        super(
                "Form with id or slug '%s' not found".formatted(idOrSlug),
                ApplicationExceptionType.NOT_FOUND,
                "FORM_NOT_FOUND");
    }
}

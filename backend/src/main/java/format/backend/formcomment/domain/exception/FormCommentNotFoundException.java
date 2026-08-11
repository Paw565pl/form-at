package format.backend.formcomment.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class FormCommentNotFoundException extends ApplicationException {

    public FormCommentNotFoundException(String id) {
        super(
                "Form comment with id: '%s' was not found".formatted(id),
                ApplicationExceptionType.NOT_FOUND,
                "FORM_COMMENT_NOT_FOUND");
    }
}

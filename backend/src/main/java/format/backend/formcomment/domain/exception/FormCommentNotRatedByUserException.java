package format.backend.formcomment.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class FormCommentNotRatedByUserException extends ApplicationException {

    public FormCommentNotRatedByUserException(String commentId) {
        super(
                "Form comment wih id '%s' has not been rated by the user".formatted(commentId),
                ApplicationExceptionType.NOT_FOUND,
                "FORM_COMMENT_NOT_RATED_BY_USER");
    }
}

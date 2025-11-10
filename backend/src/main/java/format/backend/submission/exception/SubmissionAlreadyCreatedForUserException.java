package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class SubmissionAlreadyCreatedForUserException extends ApplicationException {

    public SubmissionAlreadyCreatedForUserException(String formIdOrSlug) {
        super(HttpStatus.CONFLICT, String.format("You have already submitted form with id or slug '%s'", formIdOrSlug));
    }
}

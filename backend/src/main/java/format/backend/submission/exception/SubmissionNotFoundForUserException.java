package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class SubmissionNotFoundForUserException extends ApplicationException {

    public SubmissionNotFoundForUserException(String formIdOrSlug) {
        super(
                HttpStatus.NOT_FOUND,
                String.format("You have not submitted your answers for form with id or slug '%s'", formIdOrSlug));
    }
}

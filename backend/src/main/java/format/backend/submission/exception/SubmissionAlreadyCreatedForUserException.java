package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class SubmissionAlreadyCreatedForUserException extends ApplicationException {

    public SubmissionAlreadyCreatedForUserException(@NonNull String formIdOrSlug) {
        super(
                HttpStatus.CONFLICT,
                String.format("Form with id or slug '%s' has been already submitted by the user", formIdOrSlug));
    }
}

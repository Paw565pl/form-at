package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class SubmissionAlreadyCreatedForUserException extends ApplicationException {

    public SubmissionAlreadyCreatedForUserException(@NonNull String formIdOrSlug) {
        super(
                HttpStatus.CONFLICT,
                String.format("You have already submitted answers for form with id or slug '%s'", formIdOrSlug));
    }
}

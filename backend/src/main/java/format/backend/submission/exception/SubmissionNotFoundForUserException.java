package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class SubmissionNotFoundForUserException extends ApplicationException {

    public SubmissionNotFoundForUserException(@NonNull String formIdOrSlug) {
        super(
                HttpStatus.NOT_FOUND,
                "SUBMISSION_NOT_FOUND_FOR_USER",
                String.format("Form with id or slug '%s' has not been submitted by the user", formIdOrSlug));
    }
}

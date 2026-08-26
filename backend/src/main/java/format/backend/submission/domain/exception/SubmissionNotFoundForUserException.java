package format.backend.submission.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class SubmissionNotFoundForUserException extends ApplicationException {

    public SubmissionNotFoundForUserException(String formIdOrSlug) {
        super(
                "Form with id or slug '%s' has not been submitted by the user".formatted(formIdOrSlug),
                ApplicationExceptionType.NOT_FOUND,
                "SUBMISSION_NOT_FOUND_FOR_USER");
    }
}

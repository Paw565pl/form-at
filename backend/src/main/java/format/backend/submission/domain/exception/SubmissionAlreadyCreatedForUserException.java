package format.backend.submission.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class SubmissionAlreadyCreatedForUserException extends ApplicationException {

    public SubmissionAlreadyCreatedForUserException(String formIdOrSlug) {
        super(
                "Form with id or slug '%s' has been already submitted by the user".formatted(formIdOrSlug),
                ApplicationExceptionType.CONFLICT,
                "SUBMISSION_ALREADY_CREATED_FOR_USER");
    }
}

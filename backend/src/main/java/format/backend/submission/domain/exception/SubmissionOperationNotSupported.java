package format.backend.submission.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class SubmissionOperationNotSupported extends ApplicationException {

    public SubmissionOperationNotSupported(String formIdOrSlug) {
        super(
                "Form with id or slug '%s' does not save submissions".formatted(formIdOrSlug),
                ApplicationExceptionType.CONFLICT,
                "SUBMISSION_OPERATION_NOT_SUPPORTED");
    }
}

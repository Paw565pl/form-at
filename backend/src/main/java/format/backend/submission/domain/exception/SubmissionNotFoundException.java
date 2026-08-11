package format.backend.submission.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class SubmissionNotFoundException extends ApplicationException {

    public SubmissionNotFoundException(String submissionId) {
        super(
                "Submission with id '%s' not found".formatted(submissionId),
                ApplicationExceptionType.NOT_FOUND,
                "SUBMISSION_NOT_FOUND");
    }
}

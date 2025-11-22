package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class SubmissionNotFoundException extends ApplicationException {

    public SubmissionNotFoundException(String submissionId) {
        super(HttpStatus.NOT_FOUND, String.format("Submission with id '%s' not found", submissionId));
    }
}

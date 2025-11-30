package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class SubmissionNotFoundException extends ApplicationException {

    public SubmissionNotFoundException(@NonNull String submissionId) {
        super(HttpStatus.NOT_FOUND, String.format("Submission with id '%s' not found", submissionId));
    }
}

package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class SubmissionOperationNotSupported extends ApplicationException {

    public SubmissionOperationNotSupported(@NonNull String formIdOrSlug) {
        super(HttpStatus.CONFLICT, String.format("Form with id or slug '%s' does not save submissions", formIdOrSlug));
    }
}

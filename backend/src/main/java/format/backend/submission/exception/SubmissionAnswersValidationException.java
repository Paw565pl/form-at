package format.backend.submission.exception;

import format.backend.core.exception.ApplicationException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public final class SubmissionAnswersValidationException extends ApplicationException {

    public SubmissionAnswersValidationException(Map<String, List<String>> errors) {
        super(HttpStatus.BAD_REQUEST, "Answers validation failed", errors);
    }
}

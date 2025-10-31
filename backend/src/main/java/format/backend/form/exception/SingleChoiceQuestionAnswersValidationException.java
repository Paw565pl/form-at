package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class SingleChoiceQuestionAnswersValidationException extends ApplicationException {

    public SingleChoiceQuestionAnswersValidationException() {
        super(HttpStatus.BAD_REQUEST, "Single choice questions must have exactly one valid answer");
    }
}

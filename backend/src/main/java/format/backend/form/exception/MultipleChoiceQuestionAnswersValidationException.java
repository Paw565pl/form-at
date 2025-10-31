package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class MultipleChoiceQuestionAnswersValidationException extends ApplicationException {

    public MultipleChoiceQuestionAnswersValidationException() {
        super(
                HttpStatus.BAD_REQUEST,
                "Multiple choice questions must have at least one valid answer and at least one invalid answer");
    }
}

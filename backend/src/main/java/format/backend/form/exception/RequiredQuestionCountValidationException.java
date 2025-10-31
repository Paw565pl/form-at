package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RequiredQuestionCountValidationException extends ApplicationException {

    public RequiredQuestionCountValidationException() {
        super(HttpStatus.BAD_REQUEST, "Form must have at least one required question");
    }
}

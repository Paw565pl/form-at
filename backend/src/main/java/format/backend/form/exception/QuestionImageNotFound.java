package format.backend.form.exception;

import format.backend.core.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public final class QuestionImageNotFound extends ApplicationException {

    public QuestionImageNotFound(String key) {
        super(HttpStatus.BAD_REQUEST, String.format("Question image with key %s was not found in storage", key));
    }
}

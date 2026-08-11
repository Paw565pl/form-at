package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public final class ValidationException extends ApplicationException {

    static final String MESSAGE = "Validation failed";
    static final String CODE = "VALIDATION_FAILED";

    private final Map<String, List<String>> errors;

    public ValidationException(Map<String, List<String>> errors) {
        super(MESSAGE, ApplicationExceptionType.BAD_REQUEST, CODE);
        this.errors = Map.copyOf(errors);
    }
}

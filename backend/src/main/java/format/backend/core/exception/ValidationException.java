package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public final class ValidationException extends ApplicationException {

    public ValidationException(Map<String, List<String>> errors) {
        super(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }
}

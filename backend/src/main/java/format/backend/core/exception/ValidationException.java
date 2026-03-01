package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

public final class ValidationException extends ApplicationException {

    public ValidationException(@NonNull Map<@NonNull String, @NonNull List<@NonNull String>> errors) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Validation failed", errors);
    }
}

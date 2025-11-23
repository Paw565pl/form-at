package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    @Nullable private final Map<String, List<String>> errors;

    protected ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errors = null;
    }

    protected ApplicationException(HttpStatus status, String message, @Nullable Map<String, List<String>> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}

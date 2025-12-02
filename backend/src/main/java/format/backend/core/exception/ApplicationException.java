package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final @NonNull HttpStatus status;

    private final @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> errors;

    protected ApplicationException(@NonNull HttpStatus status, @NonNull String message) {
        super(message);
        this.status = status;
        this.errors = null;
    }

    protected ApplicationException(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}

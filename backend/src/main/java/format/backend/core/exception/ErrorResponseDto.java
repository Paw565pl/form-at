package format.backend.core.exception;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@JsonInclude(NON_NULL)
public class ErrorResponseDto {

    private final @NonNull Instant timestamp = Instant.now();

    private final int status;

    private final @NonNull String error;

    private final @NonNull String message;

    private final @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> errors;

    public ErrorResponseDto(@NonNull HttpStatus status, @NonNull String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.errors = null;
    }

    public ErrorResponseDto(
            @NonNull HttpStatus status,
            @NonNull String message,
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> errors) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.errors = errors;
    }
}

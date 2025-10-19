package format.backend.core.exception;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@ToString
@JsonInclude(NON_NULL)
public class ErrorResponseDto {

    @NonNull private final Instant timestamp = Instant.now();

    private final int status;

    @NonNull private final String error;

    @NonNull private final String message;

    @Nullable private Map<String, List<String>> errors;

    public ErrorResponseDto(@NonNull HttpStatus status, @NonNull String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }

    public ErrorResponseDto(
            @NonNull HttpStatus status, @NonNull String message, @Nullable Map<String, List<String>> errors) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.errors = errors;
    }
}

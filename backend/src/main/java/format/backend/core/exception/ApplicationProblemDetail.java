package format.backend.core.exception;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Getter
@EqualsAndHashCode(callSuper = true)
final class ApplicationProblemDetail extends ProblemDetail {

    private final String code;
    private final @Nullable Map<String, List<String>> errors;

    @Builder
    public ApplicationProblemDetail(
            HttpStatus status, @Nullable String detail, String code, @Nullable Map<String, List<String>> errors) {
        setStatus(Objects.requireNonNull(status));
        setDetail(detail);
        this.code = Objects.requireNonNull(code);
        this.errors = errors != null && !errors.isEmpty() ? Map.copyOf(errors) : null;
    }

    public static ApplicationProblemDetail createValidation(Map<String, List<String>> errors) {
        return ApplicationProblemDetail.builder()
                .status(HttpStatus.BAD_REQUEST)
                .detail(ValidationException.MESSAGE)
                .code(ValidationException.CODE)
                .errors(errors)
                .build();
    }
}

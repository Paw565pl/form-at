package format.backend.core.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static ProblemDetail createProblemDetail(
            @NonNull HttpStatusCode status,
            @NonNull String detail,
            @NonNull String code,
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> errors) {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail);

        problemDetail.setProperty("code", code);
        if (errors != null && !errors.isEmpty()) {
            problemDetail.setProperty("errors", errors);
        }

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        val status = HttpStatus.BAD_REQUEST;
        val errors = e.getFieldErrors().stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null
                        && !fieldError.getDefaultMessage().isBlank())
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())));
        val problemDetail = createProblemDetail(status, "Validation failed", "VALIDATION_FAILED", errors);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException e) {
        val status = HttpStatus.BAD_REQUEST;
        val errors = e.getConstraintViolations().stream()
                .map(error -> {
                    val propertyPathIterator = error.getPropertyPath().iterator();

                    var fieldName = "";
                    while (propertyPathIterator.hasNext()) {
                        fieldName = propertyPathIterator.next().getName();
                    }

                    return Map.entry(fieldName, error.getMessage());
                })
                .filter(entry -> !entry.getKey().isBlank()
                        && entry.getValue() != null
                        && !entry.getValue().isBlank())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        val problemDetail = createProblemDetail(status, "Validation failed", "VALIDATION_FAILED", errors);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        val status = HttpStatus.UNPROCESSABLE_CONTENT;
        val errors =
                switch (e.getCause()) {
                    case InvalidFormatException invalidFormatException
                    when invalidFormatException.getTargetType().isEnum()
                            && !invalidFormatException.getPath().isEmpty() -> {
                        val fieldName =
                                invalidFormatException.getPath().getLast().getPropertyName();
                        val givenInput = invalidFormatException.getValue();
                        val validValues = Arrays.stream(
                                        invalidFormatException.getTargetType().getEnumConstants())
                                .map(Object::toString)
                                .toList();

                        val message = String.format(
                                "Invalid value '%s'. Accepted values are: %s",
                                givenInput, String.join(", ", validValues));
                        yield Map.of(fieldName, List.of(message));
                    }
                    default -> null;
                };
        val problemDetail =
                createProblemDetail(status, "Malformed or missing JSON request body", "MALFORMED_JSON_BODY", errors);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        val status = HttpStatus.CONFLICT;
        val problemDetail = createProblemDetail(status, "A database conflict occurred", "INTEGRITY_VIOLATION", null);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ProblemDetail> handlePropertyReferenceException(PropertyReferenceException e) {
        val status = HttpStatus.BAD_REQUEST;
        val problemDetail = createProblemDetail(status, e.getMessage(), "INVALID_REFERENCE", null);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleApplicationException(ApplicationException e) {
        val status = e.getStatus();
        val problemDetail = createProblemDetail(status, e.getMessage(), e.getCode(), e.getErrors());

        return ResponseEntity.status(status).body(problemDetail);
    }
}

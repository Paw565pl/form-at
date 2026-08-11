package format.backend.core.exception;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
@Order(HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String EXCEPTION_SUFFIX = "Exception";
    private static final String DEFAULT_VALIDATION_MESSAGE = "Invalid value";
    private static final AuthenticationTrustResolver authenticationTrustResolver =
            new AuthenticationTrustResolverImpl();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApplicationProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        val errors = e.getFieldErrors().stream()
                .filter(error -> error.getDefaultMessage() != null
                        && !error.getDefaultMessage().isBlank())
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                error -> {
                                    if (error.isBindingFailure()) {
                                        val fieldType = e.getFieldType(error.getField());
                                        if (fieldType != null && fieldType.isEnum()) {
                                            return createEnumValidationError(fieldType);
                                        }

                                        return DEFAULT_VALIDATION_MESSAGE;
                                    }

                                    val defaultMessage = error.getDefaultMessage();
                                    return defaultMessage != null && !defaultMessage.isBlank()
                                            ? defaultMessage
                                            : DEFAULT_VALIDATION_MESSAGE;
                                },
                                Collectors.toUnmodifiableList())));
        val problemDetail = ApplicationProblemDetail.createValidation(errors);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ApplicationProblemDetail> handleHandlerMethodValidationException(
            HandlerMethodValidationException e) {
        val errors = e.getParameterValidationResults().stream()
                .filter(result -> result.getMethodParameter().getParameterName() != null)
                .collect(Collectors.toUnmodifiableMap(
                        result -> result.getMethodParameter().getParameterName(),
                        result -> result.getResolvableErrors().stream()
                                .map(MessageSourceResolvable::getDefaultMessage)
                                .filter(Objects::nonNull)
                                .toList()));
        val problemDetail = ApplicationProblemDetail.createValidation(errors);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApplicationProblemDetail> handleConstraintViolationException(ConstraintViolationException e) {
        val errors = e.getConstraintViolations().stream()
                .map(violation -> {
                    var fieldName = "";
                    for (val path : violation.getPropertyPath()) {
                        fieldName = path.getName();
                    }

                    return Map.entry(fieldName, violation.getMessage());
                })
                .filter(entry -> entry.getKey() != null
                        && !entry.getKey().isBlank()
                        && entry.getValue() != null
                        && !entry.getValue().isBlank())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toUnmodifiableList())));
        val problemDetail = ApplicationProblemDetail.createValidation(errors);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApplicationProblemDetail> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        val errors =
                switch (e.getCause()) {
                    case InvalidFormatException ex
                    when ex.getTargetType().isEnum() && !ex.getPath().isEmpty() -> {
                        val fieldName = ex.getPath().stream()
                                .map(reference -> reference.getIndex() != -1
                                        ? "[%s].".formatted(reference.getIndex())
                                        : reference.getPropertyName())
                                .collect(Collectors.joining());
                        val message = createEnumValidationError(ex.getTargetType());

                        yield Map.of(fieldName, List.of(message));
                    }
                    case null, default -> null;
                };
        val problemDetail = ApplicationProblemDetail.builder()
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .detail("Malformed or missing request body")
                .code("MALFORMED_REQUEST_BODY")
                .errors(errors)
                .build();

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    private static String createEnumValidationError(Class<?> type) {
        val validValues =
                Arrays.stream(type.getEnumConstants()).map(Object::toString).collect(Collectors.joining(", "));
        return "%s. Valid values are: %s".formatted(DEFAULT_VALIDATION_MESSAGE, validValues);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApplicationProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        val problemDetail = ApplicationProblemDetail.builder()
                .status(HttpStatus.CONFLICT)
                .detail("A database conflict occurred")
                .code("INTEGRITY_VIOLATION")
                .build();
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ApplicationProblemDetail> handleOptimisticLockingFailureException(
            OptimisticLockingFailureException e) {
        val problemDetail = ApplicationProblemDetail.builder()
                .status(HttpStatus.CONFLICT)
                .detail("The resource was modified by another request")
                .code("CONCURRENT_MODIFICATION")
                .build();
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApplicationProblemDetail> handleUnauthorizedException(RuntimeException e) {
        val status = HttpStatus.UNAUTHORIZED;
        val problemDetail = ApplicationProblemDetail.builder()
                .status(status)
                .detail(UnauthorizedException.MESSAGE)
                .code(status.name())
                .build();

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApplicationProblemDetail> handleForbiddenException(RuntimeException e) {
        val isAuthenticated = authenticationTrustResolver.isAuthenticated(
                SecurityContextHolder.getContext().getAuthentication());
        if (!isAuthenticated) return handleUnauthorizedException(e);

        val status = HttpStatus.FORBIDDEN;
        val problemDetail = ApplicationProblemDetail.builder()
                .status(status)
                .detail(ForbiddenException.MESSAGE)
                .code(status.name())
                .build();

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ApplicationException.class)
    ResponseEntity<ApplicationProblemDetail> handleApplicationException(ApplicationException e) {
        val status =
                switch (e.getType()) {
                    case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
                    case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
                    case FORBIDDEN -> HttpStatus.FORBIDDEN;
                    case NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case CONFLICT -> HttpStatus.CONFLICT;
                    case TOO_MANY_REQUESTS -> HttpStatus.TOO_MANY_REQUESTS;
                    case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
                };
        val problemDetail = ApplicationProblemDetail.builder()
                .status(status)
                .detail(e.getMessage())
                .code(e.getCode())
                .build();

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ApplicationProblemDetail> handleValidationException(ValidationException e) {
        val problemDetail = ApplicationProblemDetail.createValidation(e.getErrors());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApplicationProblemDetail> handleUnexpectedException(Exception e) {
        if (e instanceof ErrorResponse errorResponse) {
            val code = createCodeFromException(e);
            val defaultProblemDetail = errorResponse.getBody();
            val problemDetail = ApplicationProblemDetail.builder()
                    .status(Objects.requireNonNull(HttpStatus.resolve(defaultProblemDetail.getStatus())))
                    .detail(defaultProblemDetail.getDetail())
                    .code(code)
                    .build();

            return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
        }

        log.error("An unhandled exception occurred.", e);

        val problemDetail = ApplicationProblemDetail.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .detail("Internal server error")
                .code("INTERNAL_SERVER_ERROR")
                .build();
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    /// Transforms exception class name into problem detail code <br/>
    /// Example: HttpRequestMethodNotSupportedException -> HTTP_REQUEST_METHOD_NOT_SUPPORTED
    private static String createCodeFromException(Exception e) {
        val name = e.getClass().getSimpleName();
        val nameWithoutExceptionSuffix =
                name.endsWith(EXCEPTION_SUFFIX) ? name.substring(0, name.length() - EXCEPTION_SUFFIX.length()) : name;

        if (nameWithoutExceptionSuffix.isBlank()) return "UNKNOWN_ERROR";
        return CAMEL_CASE_PATTERN
                .matcher(nameWithoutExceptionSuffix)
                .replaceAll("$1_$2")
                .toUpperCase(Locale.ROOT);
    }
}

package format.backend.core.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        val errors = e.getFieldErrors().stream()
                .filter(fieldError -> fieldError.getDefaultMessage() != null
                        && !fieldError.getDefaultMessage().isBlank())
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())));

        val status = HttpStatus.BAD_REQUEST;
        val response = new ErrorResponseDto(status, "Validation failed", errors);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException e) {
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

        val status = HttpStatus.BAD_REQUEST;
        val response = new ErrorResponseDto(status, "Validation failed", errors);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ignored) {
        val status = HttpStatus.BAD_REQUEST;
        val response = new ErrorResponseDto(status, "Required request body is missing");

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handleIllegalArgumentException(DataIntegrityViolationException e) {
        val status = HttpStatus.CONFLICT;
        val response = new ErrorResponseDto(status, e.getMessage());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handlePropertyReferenceException(PropertyReferenceException e) {
        val status = HttpStatus.BAD_REQUEST;
        val response = new ErrorResponseDto(status, e.getMessage());

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<@NonNull ErrorResponseDto> handleApplicationException(ApplicationException e) {
        val status = e.getStatus();
        val response = Optional.ofNullable(e.getErrors())
                .map(errors -> new ErrorResponseDto(status, e.getMessage(), errors))
                .orElse(new ErrorResponseDto(status, e.getMessage()));

        return ResponseEntity.status(status).body(response);
    }
}

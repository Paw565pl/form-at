package format.backend.core.exception;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final ApplicationExceptionType type;
    private final String code;

    protected ApplicationException(String message, ApplicationExceptionType type, String code) {
        super(message);
        this.type = type;
        this.code = code;
    }
}

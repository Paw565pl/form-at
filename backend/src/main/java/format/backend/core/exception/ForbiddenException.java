package format.backend.core.exception;

public final class ForbiddenException extends ApplicationException {

    static final String MESSAGE = "User is missing required permissions";

    public ForbiddenException() {
        super(MESSAGE, ApplicationExceptionType.FORBIDDEN, ApplicationExceptionType.FORBIDDEN.name());
    }
}

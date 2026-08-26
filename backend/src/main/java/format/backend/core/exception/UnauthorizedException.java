package format.backend.core.exception;

public final class UnauthorizedException extends ApplicationException {

    static final String MESSAGE = "Authentication is required";

    public UnauthorizedException() {
        super(MESSAGE, ApplicationExceptionType.UNAUTHORIZED, ApplicationExceptionType.UNAUTHORIZED.name());
    }
}

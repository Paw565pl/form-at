package format.backend.upload.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class UserUploadRateLimitExceededException extends ApplicationException {

    public UserUploadRateLimitExceededException() {
        super(
                "You have exceeded your upload request quota",
                ApplicationExceptionType.TOO_MANY_REQUESTS,
                "USER_UPLOAD_RATE_LIMIT_EXCEEDED");
    }
}

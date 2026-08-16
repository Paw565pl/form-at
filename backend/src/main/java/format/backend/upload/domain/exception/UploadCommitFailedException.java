package format.backend.upload.domain.exception;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class UploadCommitFailedException extends ApplicationException {

    public UploadCommitFailedException() {
        super("Failed to confirm uploads", ApplicationExceptionType.CONFLICT, "UPLOADS_COMMIT_FAILED");
    }
}

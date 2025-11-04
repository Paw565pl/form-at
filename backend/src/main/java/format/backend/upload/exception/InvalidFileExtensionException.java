package format.backend.upload.exception;

import format.backend.core.exception.ApplicationException;
import java.util.Collection;
import org.springframework.http.HttpStatus;

public final class InvalidFileExtensionException extends ApplicationException {

    public InvalidFileExtensionException(String fileExtension, Collection<String> allowedExtensions) {
        super(
                HttpStatus.BAD_REQUEST,
                String.format(
                        "File has invalid extension '%s', only %s are allowed",
                        fileExtension, String.join(", ", allowedExtensions)));
    }
}

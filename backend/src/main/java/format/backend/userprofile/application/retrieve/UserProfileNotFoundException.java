package format.backend.userprofile.application.retrieve;

import format.backend.core.exception.ApplicationException;
import format.backend.core.exception.ApplicationExceptionType;

public final class UserProfileNotFoundException extends ApplicationException {

    public UserProfileNotFoundException() {
        super("User profile not found", ApplicationExceptionType.NOT_FOUND, "USER_PROFILE_NOT_FOUND");
    }
}

package format.backend.auth;

import java.util.Optional;

public interface UserFacade {
    Optional<UserDto> retrieveById(String id);

    Optional<UserDto> retrieveByUsername(String username);

    long count();
}

package format.backend.auth;

import format.backend.auth.application.UserMapper;
import format.backend.auth.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Optional<UserDto> retrieveById(String id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    public Optional<UserDto> retrieveByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    public long count() {
        return userRepository.count();
    }
}

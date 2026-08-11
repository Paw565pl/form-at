package format.backend.auth.application;

import format.backend.auth.UserDto;
import format.backend.auth.UserFacade;
import format.backend.auth.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<UserDto> retrieveById(String id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> retrieveByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}

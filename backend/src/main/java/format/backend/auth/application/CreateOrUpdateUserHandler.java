package format.backend.auth.application;

import format.backend.auth.UserClaims;
import format.backend.auth.domain.entity.UserEntity;
import format.backend.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrUpdateUserHandler {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserEntity handle(UserClaims userClaims) {
        return userRepository.createOrUpdate(userMapper.toEntity(userClaims));
    }
}

package format.backend.auth.service;

import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity createOrUpdate(KeycloakJwtClaims jwtClaims) {
        return userRepository.save(new UserEntity(jwtClaims.sub(), jwtClaims.username(), jwtClaims.email()));
    }
}

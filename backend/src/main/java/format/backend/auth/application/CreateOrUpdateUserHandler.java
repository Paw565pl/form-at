package format.backend.auth.application;

import format.backend.auth.UserClaims;
import format.backend.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = UserCacheConfig.CACHE_NAME)
public class CreateOrUpdateUserHandler {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable
    public void handle(UserClaims userClaims) {
        userRepository.createOrUpdate(userMapper.toEntity(userClaims));
    }
}

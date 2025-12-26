package format.backend.auth.repository;

import format.backend.auth.entity.UserEntity;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<@NonNull UserEntity, @NonNull String> {
    Optional<UserEntity> findByUsername(String username);
}

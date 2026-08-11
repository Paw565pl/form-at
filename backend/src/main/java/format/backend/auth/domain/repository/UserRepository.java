package format.backend.auth.domain.repository;

import format.backend.auth.domain.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String>, UserRepositoryCustom {

    Optional<UserEntity> findByUsername(String username);
}

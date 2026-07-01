package format.backend.auth.datafactory;

import format.backend.auth.entity.UserEntity;
import java.util.UUID;

public abstract class UserTestDataFactory {

    public static UserEntity create() {
        return new UserEntity(UUID.randomUUID().toString(), "test user", "test@local.dev");
    }
}

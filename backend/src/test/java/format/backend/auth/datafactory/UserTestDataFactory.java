package format.backend.auth.datafactory;

import format.backend.auth.domain.entity.UserEntity;
import java.util.UUID;
import lombok.val;

public final class UserTestDataFactory {

    private UserTestDataFactory() {}

    public static UserEntity create() {
        val uuid = UUID.randomUUID().toString();
        return new UserEntity(uuid, "test user - " + uuid, "test@local.dev");
    }
}

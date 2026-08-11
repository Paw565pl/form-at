package format.backend.auth;

import java.util.Optional;
import org.jspecify.annotations.Nullable;

public enum Role {
    ADMIN;

    public static Optional<Role> fromString(@Nullable String value) {
        if (Role.ADMIN.name().equalsIgnoreCase(value)) return Optional.of(ADMIN);
        return Optional.empty();
    }
}

package format.backend.auth.entity;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor
@SuppressWarnings("java:S6548")
public enum Role {
    ADMIN("admin");

    private final String value;

    /**
     * Returns the role with prefix "ROLE_".
     */
    public String getPrefixedValue() {
        return "ROLE_" + value;
    }

    private static final Role[] VALUES = values();

    public static Optional<Role> fromValue(String value) {
        if (value == null) return Optional.empty();

        for (val role : VALUES) {
            if (role.getValue().equalsIgnoreCase(value)) {
                return Optional.of(role);
            }
        }

        return Optional.empty();
    }
}

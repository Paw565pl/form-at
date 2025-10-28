package format.backend.auth.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@SuppressWarnings("java:S6548")
public enum Role {
    ADMIN("admin");

    private final String value;

    private static final Map<String, Role> valueToRoleMap =
            Arrays.stream(values()).collect(Collectors.toMap(r -> r.value.toLowerCase(), r -> r));

    /**
     * Returns the role with prefix "ROLE_".
     */
    public String getPrefixedValue() {
        return "ROLE_" + value;
    }

    public static Optional<Role> fromValue(String value) {
        return Optional.ofNullable(valueToRoleMap.get(value.toLowerCase()));
    }
}

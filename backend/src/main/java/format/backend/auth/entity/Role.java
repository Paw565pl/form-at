package format.backend.auth.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

    private static final Map<String, Role> valueToRoleMap =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(r -> r.value, Function.identity()));

    public static Optional<Role> from(String role) {
        return Optional.ofNullable(valueToRoleMap.get(role.toLowerCase()));
    }
}

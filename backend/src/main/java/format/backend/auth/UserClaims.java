package format.backend.auth;

import java.util.Objects;
import java.util.Set;
import lombok.Builder;

@Builder
public record UserClaims(String id, String username, String email, Set<Role> roles) {
    public UserClaims {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);
        Objects.requireNonNull(email);
        roles = Set.copyOf(Objects.requireNonNull(roles));
    }
}

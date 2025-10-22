package format.backend.auth.jwt;

import format.backend.auth.entity.Role;
import java.util.Set;
import org.springframework.lang.NonNull;

public record KeycloakJwtClaims(
        @NonNull String sub,
        @NonNull String username,
        @NonNull String email,
        @NonNull Set<Role> roles) {}

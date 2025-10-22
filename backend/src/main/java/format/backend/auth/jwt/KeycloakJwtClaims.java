package format.backend.auth.jwt;

import format.backend.auth.entity.Role;
import java.util.Set;

public record KeycloakJwtClaims(String sub, String username, String email, Set<Role> roles) {}

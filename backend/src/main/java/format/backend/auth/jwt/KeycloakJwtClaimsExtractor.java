package format.backend.auth.jwt;

import format.backend.auth.entity.Role;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakJwtClaimsExtractor {

    public KeycloakJwtClaims toClaims(Jwt jwt) {
        val sub = jwt.getSubject();
        val username = jwt.getClaimAsString("preferred_username");
        val email = jwt.getClaimAsString("email");
        val roles = extractRoles(jwt);

        return new KeycloakJwtClaims(sub, username, email, roles);
    }

    private Set<Role> extractRoles(Jwt jwt) {
        val realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return Set.of();

        val rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof Collection<?>)) return Set.of();

        @SuppressWarnings("unchecked")
        val roles = (Collection<String>) rolesObj;

        return roles.stream().map(Role::fromValue).flatMap(Optional::stream).collect(Collectors.toSet());
    }
}

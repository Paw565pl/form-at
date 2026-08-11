package format.backend.auth.datafactory;

import format.backend.auth.Role;
import format.backend.auth.domain.entity.UserEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.springframework.security.oauth2.jwt.Jwt;

public abstract class JwtTestFactory {

    public static Jwt create(UserEntity userEntity) {
        return create(userEntity, List.of());
    }

    public static Jwt create(UserEntity userEntity, Collection<Role> roles) {
        val roleValues = roles.stream().map(Role::name).toList();
        val now = Instant.now();

        return Jwt.withTokenValue("mock-token")
                .header("alg", "HS256")
                .issuer("self")
                .header("typ", "JWT")
                .claim("sub", userEntity.getId())
                .claim("preferred_username", userEntity.getUsername())
                .claim("email", userEntity.getEmail())
                .claim("realm_access", Map.of("roles", roleValues))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();
    }
}

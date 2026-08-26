package format.backend.auth.config;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.auth.application.CreateOrUpdateUserHandler;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final TaskExecutor taskExecutor;
    private final CreateOrUpdateUserHandler createOrUpdateUserHandler;

    public KeycloakJwtConverter(
            @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor,
            CreateOrUpdateUserHandler createOrUpdateUserHandler) {
        this.taskExecutor = taskExecutor;
        this.createOrUpdateUserHandler = createOrUpdateUserHandler;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        val id = jwt.getSubject();
        val username = jwt.getClaimAsString("preferred_username");
        val email = jwt.getClaimAsString("email");
        val roles = extractRoles(jwt);

        if (id == null || username == null || email == null || roles == null) {
            log.warn("JWT token is missing required claims.");
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_TOKEN);
        }

        val userClaims = UserClaims.builder()
                .id(id)
                .username(username)
                .email(email)
                .roles(roles)
                .build();
        taskExecutor.execute(() -> createOrUpdateUserHandler.handle(userClaims));

        val authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .toList();
        return new JwtAuthenticationToken(jwt, userClaims, authorities);
    }

    private static @Nullable Set<Role> extractRoles(Jwt jwt) {
        val realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return null;

        if (!(realmAccess.get("roles") instanceof Collection<?> roles)) return null;

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Role::fromString)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());
    }
}

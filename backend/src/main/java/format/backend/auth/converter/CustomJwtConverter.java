package format.backend.auth.converter;

import format.backend.auth.jwt.KeycloakJwtExtractor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final KeycloakJwtExtractor keycloakJwtExtractor;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        val authorities = keycloakJwtExtractor.toClaims(jwt).roles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getPrefixedValue()))
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}

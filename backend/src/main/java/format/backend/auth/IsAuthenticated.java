package format.backend.auth;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RUNTIME)
@Target({METHOD, TYPE})
@PreAuthorize("isAuthenticated()")
public @interface IsAuthenticated {}

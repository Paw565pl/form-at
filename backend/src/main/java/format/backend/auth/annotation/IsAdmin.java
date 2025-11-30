package format.backend.auth.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({METHOD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(format.backend.auth.entity.Role).ADMIN.value)")
public @interface IsAdmin {}

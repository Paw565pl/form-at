package format.backend.auth.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("cors")
public record CorsProperties(
        @NotEmpty List<@NotBlank String> allowedHeaders,

        @NotEmpty List<@NotBlank String> allowedMethods,

        @NotEmpty List<@NotBlank String> allowedOrigins) {}

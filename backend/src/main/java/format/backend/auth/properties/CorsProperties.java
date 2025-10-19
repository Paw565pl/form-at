package format.backend.auth.properties;

import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@Validated
@ConfigurationProperties("cors")
public class CorsProperties {

    private @NotEmpty List<String> allowedHeaders = new ArrayList<>();
    private @NotEmpty List<String> allowedMethods = new ArrayList<>();
    private @NotEmpty List<String> allowedOrigins = new ArrayList<>();
}

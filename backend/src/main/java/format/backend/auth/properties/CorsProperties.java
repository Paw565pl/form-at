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

    @NotEmpty private List<String> allowedHeaders = new ArrayList<>();

    @NotEmpty private List<String> allowedMethods = new ArrayList<>();

    @NotEmpty private List<String> allowedOrigins = new ArrayList<>();
}

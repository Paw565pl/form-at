package format.backend.storage.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@Validated
@ConfigurationProperties("minio")
public class MinioProperties {

    @NotBlank private String endpoint;

    @NotBlank private String accessKey;

    @NotBlank private String secretKey;
}

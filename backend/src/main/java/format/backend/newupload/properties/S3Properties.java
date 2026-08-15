package format.backend.newupload.properties;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.s3")
public record S3Properties(
        @URL @NotBlank String url,

        @NotBlank String region,

        @NotBlank String accessKey,

        @NotBlank String secretKey,

        @NotBlank String bucket) {}

package format.backend.upload.properties;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("s3")
public record S3Properties(
        @NotBlank @NonNull String url,

        @NotBlank @NonNull String region,

        @NotBlank @NonNull String accessKey,

        @NotBlank @NonNull String secretKey,

        @NotBlank @NonNull String bucket) {}

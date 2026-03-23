package format.backend.upload.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.regions.Region;

@Getter
@Setter
@ToString
@Validated
@ConfigurationProperties("s3")
public class S3Properties {

    private @NotBlank @NonNull String url;

    private @NotNull @NonNull Boolean forcePathStyle;

    private @NotNull @NonNull Region region;

    private @NotBlank @NonNull String accessKey;

    private @NotBlank @NonNull String secretKey;

    private @NotBlank @NonNull String bucket;
}

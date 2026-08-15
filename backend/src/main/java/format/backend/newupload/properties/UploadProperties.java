package format.backend.newupload.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.uploads")
public record UploadProperties(
        @DefaultValue("5MB") @NotNull DataSize maxSize,
        @DefaultValue @Valid Expiration expiration,
        @DefaultValue @Valid RateLimit rateLimit,
        @DefaultValue @Valid Concurrency concurrency) {

    public record Expiration(
            @DefaultValue("10m") @NotNull Duration postPolicy,
            @DefaultValue("24h") @NotNull Duration presignedGetUrl,
            @DefaultValue("24h") @NotNull Duration pendingUploads) {}

    public record RateLimit(
            @DefaultValue("1d") @NotNull Duration window,
            @DefaultValue("220") @Positive int maxUploadsInWindow) {}

    public record Concurrency(@DefaultValue("200") @Positive int maxGetOperations) {}
}

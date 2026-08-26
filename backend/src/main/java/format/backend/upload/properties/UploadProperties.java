package format.backend.upload.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
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

            @DurationMax(days = 7) @DefaultValue("24h") @NotNull Duration presignedGetUrl,

            @DurationMin(hours = 1) @DefaultValue("24h") @NotNull Duration pendingUploads) {
        public Expiration {
            if (postPolicy.toSeconds() >= pendingUploads.toSeconds()) {
                throw new IllegalArgumentException("pendingUploads must be greater than postPolicy");
            }
        }
    }

    public record RateLimit(
            @DefaultValue("1d") @NotNull Duration window,
            @DefaultValue("220") @Positive int maxUploadsInWindow) {}

    public record Concurrency(@DefaultValue("200") @Positive int maxGetOperations) {}
}

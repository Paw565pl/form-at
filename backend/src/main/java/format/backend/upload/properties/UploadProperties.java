package format.backend.upload.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import lombok.val;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.uploads")
public record UploadProperties(
        @DefaultValue("5MB") @NotNull DataSize maxContentLength,
        @DefaultValue @Valid Expiration expiration,
        @DefaultValue @Valid RateLimit rateLimit,
        @DefaultValue @Valid Commit commit,
        @DefaultValue @Valid Retention retention,
        @DefaultValue @Valid Concurrency concurrency) {

    public record Expiration(
            @DefaultValue("10m") @NotNull Duration postPolicy,
            @DefaultValue("1h") @NotNull Duration presignedGetUrl) {}

    public record RateLimit(
            @DefaultValue("1d") @NotNull Duration window,
            @DefaultValue("220") @Positive int maxUploadsInWindow) {}

    public record Commit(@DefaultValue("2h") @NotNull Duration maxRetryWindow) {}

    public record Retention(
            @DefaultValue("temp/") @Pattern(regexp = ".+/$") @NotBlank String tempObjectPrefix,

            @DefaultValue("1") @Positive int tempObjectDeleteLifecycleDays,

            @DefaultValue("7d") @NotNull Duration staleUploadsWindow) {}

    public record Concurrency(
            @DefaultValue("150") @Positive int maxCopyOperations,
            @DefaultValue("150") @Positive int maxGetOperations) {}

    public UploadProperties {
        val hasValidDurations = retention.staleUploadsWindow().toSeconds()
                        > rateLimit.window().toSeconds()
                && rateLimit.window().toSeconds() > expiration.postPolicy().toSeconds()
                && retention.staleUploadsWindow().toSeconds()
                        > commit.maxRetryWindow().toSeconds()
                && Duration.ofDays(retention.tempObjectDeleteLifecycleDays()).compareTo(commit.maxRetryWindow()) > 0;
        if (!hasValidDurations) {
            throw new IllegalArgumentException("""
                    Config must satisfy the rules:
                    retention.staleUploadsWindow > rateLimit.window > expiration.postPolicy,
                    retention.staleUploadsWindow > commit.maxRetryWindow,
                    retention.tempObjectDeleteLifecycleDays > commit.maxRetryWindow
                    """);
        }
    }
}

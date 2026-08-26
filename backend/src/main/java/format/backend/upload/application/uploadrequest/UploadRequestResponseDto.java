package format.backend.upload.application.uploadrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import org.springframework.http.HttpHeaders;

public record UploadRequestResponseDto(
        // s3 fields
        @JsonProperty(X_AMZ_DATE) String xAmzDate,
        @JsonProperty(X_AMZ_SIGNATURE) String xAmzSignature,
        @JsonProperty(X_AMZ_ALGORITHM) String xAmzAlgorithm,
        @JsonProperty(X_AMZ_CREDENTIAL) String xAmzCredential,
        String policy,

        // custom fields
        @JsonProperty(X_AMZ_META_FILENAME) String filename,
        @JsonProperty(X_AMZ_META_USER_ID) String userId,
        String key,
        @JsonProperty(HttpHeaders.CONTENT_TYPE) String contentType) {
    private static final String X_AMZ_DATE = "X-Amz-Date";
    private static final String X_AMZ_SIGNATURE = "x-amz-signature";
    private static final String X_AMZ_ALGORITHM = "x-amz-algorithm";
    private static final String X_AMZ_CREDENTIAL = "x-amz-credential";
    private static final String X_AMZ_META_FILENAME = "x-amz-meta-filename";
    private static final String X_AMZ_META_USER_ID = "x-amz-meta-user-id";

    @Builder
    public static UploadRequestResponseDto fromFormData(
            Map<String, String> formData, String filename, String userId, String key, String contentType) {
        return new UploadRequestResponseDto(
                Objects.requireNonNull(formData.get(X_AMZ_DATE)),
                Objects.requireNonNull(formData.get(X_AMZ_SIGNATURE)),
                Objects.requireNonNull(formData.get(X_AMZ_ALGORITHM)),
                Objects.requireNonNull(formData.get(X_AMZ_CREDENTIAL)),
                Objects.requireNonNull(formData.get("policy")),
                filename,
                userId,
                key,
                contentType);
    }
}

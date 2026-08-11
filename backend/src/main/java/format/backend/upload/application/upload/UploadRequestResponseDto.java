package format.backend.upload.application.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import org.springframework.http.HttpHeaders;

public record UploadRequestResponseDto(
        // s3 fields
        @JsonProperty(xAmzDateJson) String xAmzDate,
        @JsonProperty(xAmzSignatureJson) String xAmzSignature,
        @JsonProperty(xAmzAlgorithmJson) String xAmzAlgorithm,
        @JsonProperty(xAmzCredentialJson) String xAmzCredential,
        String policy,

        // custom fields
        @JsonProperty(filenameJson) String filename,
        @JsonProperty(userIdJson) String userId,
        String key,
        @JsonProperty(HttpHeaders.CONTENT_TYPE) String contentType) {
    private static final String xAmzDateJson = "X-Amz-Date";
    private static final String xAmzSignatureJson = "x-amz-signature";
    private static final String xAmzAlgorithmJson = "x-amz-algorithm";
    private static final String xAmzCredentialJson = "x-amz-credential";
    private static final String policyJson = "policy";
    private static final String filenameJson = "x-amz-meta-filename";
    private static final String userIdJson = "x-amz-meta-user-id";

    @Builder
    public static UploadRequestResponseDto fromFormData(
            Map<String, String> formData, String filename, String userId, String key, String contentType) {
        return new UploadRequestResponseDto(
                Objects.requireNonNull(formData.get(xAmzDateJson)),
                Objects.requireNonNull(formData.get(xAmzSignatureJson)),
                Objects.requireNonNull(formData.get(xAmzAlgorithmJson)),
                Objects.requireNonNull(formData.get(xAmzCredentialJson)),
                Objects.requireNonNull(formData.get(policyJson)),
                filename,
                userId,
                key,
                contentType);
    }
}

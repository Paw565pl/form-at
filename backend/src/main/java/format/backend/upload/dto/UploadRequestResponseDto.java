package format.backend.upload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;

public record UploadRequestResponseDto(
        // s3 fields
        @JsonProperty(xAmzDateJson) @NonNull String xAmzDate,
        @JsonProperty(xAmzSignatureJson) @NonNull String xAmzSignature,
        @JsonProperty(xAmzAlgorithmJson) @NonNull String xAmzAlgorithm,
        @JsonProperty(xAmzCredentialJson) @NonNull String xAmzCredential,
        @NonNull String policy,

        // custom fields
        @NonNull String key,
        @NonNull String filename,
        @JsonProperty(HttpHeaders.CONTENT_TYPE) @NonNull String contentType) {
    private static final String xAmzDateJson = "X-Amz-Date";
    private static final String xAmzSignatureJson = "x-amz-signature";
    private static final String xAmzAlgorithmJson = "x-amz-algorithm";
    private static final String xAmzCredentialJson = "x-amz-credential";
    private static final String policyJson = "policy";

    public static UploadRequestResponseDto fromFormData(
            Map<String, String> formData, String key, String filename, String contentType) {
        return new UploadRequestResponseDto(
                formData.get(xAmzDateJson),
                formData.get(xAmzSignatureJson),
                formData.get(xAmzAlgorithmJson),
                formData.get(xAmzCredentialJson),
                formData.get(policyJson),
                key,
                filename,
                contentType);
    }
}

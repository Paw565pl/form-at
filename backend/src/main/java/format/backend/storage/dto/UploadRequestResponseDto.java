package format.backend.storage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;

public record UploadRequestResponseDto(
        @JsonProperty(fileNameJson) @NonNull String fileName,
        @JsonProperty(xAmzDateJson) @NonNull String xAmzDate,
        @JsonProperty(xAmzSignatureJson) @NonNull String xAmzSignature,
        @JsonProperty(xAmzAlgorithmJson) @NonNull String xAmzAlgorithm,
        @NonNull String key,
        @JsonProperty(xAmzCredentialJson) @NonNull String xAmzCredential,
        @NonNull String policy,
        @JsonProperty(HttpHeaders.CONTENT_TYPE) @NonNull String contentType) {
    private static final String fileNameJson = "filename";
    private static final String xAmzDateJson = "x-amz-date";
    private static final String xAmzSignatureJson = "x-amz-signature";
    private static final String xAmzAlgorithmJson = "x-amz-algorithm";
    private static final String xAmzCredentialJson = "x-amz-credential";

    public static UploadRequestResponseDto fromFormData(
            Map<String, String> formData, String fileName, String key, String contentType) {
        return new UploadRequestResponseDto(
                fileName,
                formData.get(xAmzDateJson),
                formData.get(xAmzSignatureJson),
                formData.get(xAmzAlgorithmJson),
                key,
                formData.get(xAmzCredentialJson),
                formData.get("policy"),
                contentType);
    }
}

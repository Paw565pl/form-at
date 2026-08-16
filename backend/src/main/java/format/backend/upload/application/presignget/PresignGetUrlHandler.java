package format.backend.upload.application.presignget;

import static io.minio.Http.Method.GET;

import format.backend.upload.properties.S3Properties;
import format.backend.upload.properties.UploadProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresignGetUrlHandler {

    private final MinioClient minioClient;

    private final S3Properties s3Properties;
    private final UploadProperties uploadProperties;

    public Optional<String> handle(@Nullable String key) {
        if (key == null) return Optional.empty();

        try {
            val url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(s3Properties.bucket())
                    .object(key)
                    .method(GET)
                    .expiry(Math.toIntExact(
                            uploadProperties.expiration().presignedGetUrl().toSeconds()))
                    .build());
            return Optional.of(url);
        } catch (MinioException e) {
            log.warn("Creating presigned URL failed. key={}", key, e);
            return Optional.empty();
        }
    }
}

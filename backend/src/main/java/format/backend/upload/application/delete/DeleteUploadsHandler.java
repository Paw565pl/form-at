package format.backend.upload.application.delete;

import format.backend.upload.properties.S3Properties;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUploadsHandler {

    private final MinioClient minioClient;
    private final S3Properties s3Properties;

    /// Returns true if all keys were deleted successfully
    public boolean handle(Collection<String> keys) {
        if (keys.isEmpty()) return true;

        val deleteObjects = keys.stream().map(DeleteRequest.Object::new).toList();
        val deleteResults = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(s3Properties.bucket())
                .objects(deleteObjects)
                .delayMs(500)
                .maxRetries(3)
                .build());

        var isSuccess = true;
        for (val result : deleteResults) {
            try {
                val error = result.get();
                log.warn("Upload delete failed. error={}", error);
            } catch (MinioException e) {
                log.warn("Upload delete error read failed.", e);
            }

            isSuccess = false;
        }

        return isSuccess;
    }
}

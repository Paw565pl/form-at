package format.backend.newupload.application.delete;

import format.backend.newupload.properties.S3Properties;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUploadsHandler {

    private final MinioClient minioClient;
    private final S3Properties s3Properties;

    public long handle(Set<String> keys) {
        if (keys.isEmpty()) return 0;

        val deleteObjects = keys.stream().map(DeleteRequest.Object::new).toList();
        val deleteObjectsErrors = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(s3Properties.bucket())
                .objects(deleteObjects)
                .delayMs(500)
                .maxRetries(3)
                .build());

        var deletedCount = keys.size();
        for (val errorResult : deleteObjectsErrors) {
            try {
                val error = errorResult.get();
                log.warn("Upload delete failed. error={}", error);
            } catch (MinioException e) {
                log.warn("Upload delete error read failed.", e);
            }

            deletedCount--;
        }

        return deletedCount;
    }
}

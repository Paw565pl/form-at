package format.backend.upload.application.delete;

import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.S3Properties;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import java.util.HashSet;
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
    private final UploadRepository uploadRepository;

    public long handle(Set<String> keys) {
        if (keys.isEmpty()) return 0;

        val deleteObjects = keys.stream().map(DeleteRequest.Object::new).toList();
        val deleteObjectsErrors = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(s3Properties.bucket())
                .objects(deleteObjects)
                .delayMs(500)
                .maxRetries(3)
                .build());

        val deletedKeys = new HashSet<>(keys);
        var didFailUnexpectedly = false;
        for (val errorResult : deleteObjectsErrors) {
            try {
                val error = errorResult.get();
                deletedKeys.remove(error.objectName());
                log.warn("Upload delete failed. error={}", error);
            } catch (MinioException e) {
                didFailUnexpectedly = true;
                log.warn("Upload delete error read failed.", e);
            }
        }

        if (didFailUnexpectedly) return 0;
        return uploadRepository.deleteAllByKeyIn(deletedKeys);
    }
}

package format.backend.upload.application.validate;

import format.backend.auth.UserClaims;
import format.backend.upload.domain.entity.UploadEntity;
import format.backend.upload.domain.entity.UploadStatus;
import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.S3Properties;
import format.backend.upload.properties.UploadProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetInvalidUploadKeysHandler {

    private static final byte[] RIFF_HEADER_BYTES = {'R', 'I', 'F', 'F'};
    private static final byte[] WEBP_HEADER_BYTES = {'W', 'E', 'B', 'P'};

    private final MinioClient minioClient;
    private final AsyncTaskExecutor asyncTaskExecutor;

    private final S3Properties s3Properties;
    private final UploadRepository uploadRepository;

    private final Semaphore getObjectSemaphore;

    public GetInvalidUploadKeysHandler(
            MinioClient minioClient,
            @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor,
            S3Properties s3Properties,
            UploadRepository uploadRepository,
            UploadProperties uploadProperties) {
        this.minioClient = minioClient;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.s3Properties = s3Properties;
        this.uploadRepository = uploadRepository;
        this.getObjectSemaphore = new Semaphore(uploadProperties.concurrency().maxGetOperations(), true);
    }

    public Set<String> handle(Set<String> tempKeys, UserClaims userClaims) {
        if (tempKeys.isEmpty()) return Set.of();

        val validDbKeys =
                uploadRepository
                        .findAllByTempKeyInAndUserIdAndStatus(tempKeys, userClaims.id(), UploadStatus.PENDING)
                        .stream()
                        .map(UploadEntity::getTempKey)
                        .collect(Collectors.toUnmodifiableSet());
        if (tempKeys.size() != validDbKeys.size()) {
            return tempKeys.stream().filter(key -> !validDbKeys.contains(key)).collect(Collectors.toUnmodifiableSet());
        }

        val validateObjectFutures = tempKeys.stream()
                .map(key -> asyncTaskExecutor.submitCompletable(() -> validateObject(key)))
                .toList();
        val validKeys = validateObjectFutures.stream()
                .map(CompletableFuture::join)
                .flatMap(Optional::stream)
                .collect(Collectors.toUnmodifiableSet());

        return tempKeys.stream().filter(key -> !validKeys.contains(key)).collect(Collectors.toUnmodifiableSet());
    }

    /// Returns input key if object is valid
    private Optional<String> validateObject(String key) {
        try {
            getObjectSemaphore.acquire();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }

        try (val stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(s3Properties.bucket())
                .object(key)
                .offset(0L)
                .length(12L)
                .build())) {

            val header = stream.readAllBytes();
            if (header.length < 12) return Optional.empty();

            val isWebp = Arrays.equals(header, 0, 4, RIFF_HEADER_BYTES, 0, RIFF_HEADER_BYTES.length)
                    && Arrays.equals(header, 8, 12, WEBP_HEADER_BYTES, 0, WEBP_HEADER_BYTES.length);

            return isWebp ? Optional.of(key) : Optional.empty();
        } catch (ErrorResponseException _) {
            // this is thrown for 404 not found
            return Optional.empty();
        } catch (MinioException | IOException e) {
            log.warn("Validating upload object failed. key={}", key, e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Validating upload object failed unexpectedly. key={}", key, e);
            return Optional.empty();
        } finally {
            getObjectSemaphore.release();
        }
    }
}

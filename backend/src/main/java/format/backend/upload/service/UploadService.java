package format.backend.upload.service;

import static io.minio.Http.Method.GET;

import com.github.slugify.Slugify;
import format.backend.upload.dto.BatchUploadRequestDto;
import format.backend.upload.dto.UploadRequestResponseDto;
import format.backend.upload.entity.ImageType;
import format.backend.upload.entity.PendingUploadEntity;
import format.backend.upload.properties.S3Properties;
import format.backend.upload.repository.PendingUploadRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.RemoveObjectsArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteRequest;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final S3Properties s3Properties;
    private final MinioClient minioClient;

    private final Slugify slugify;
    private final PendingUploadRepository pendingUploadRepository;

    private static final int MAX_CONTENT_LENGTH = 10 * 1024 * 1024; // 10 MB
    private static final Duration UPLOAD_EXPIRY_DURATION = Duration.ofMinutes(15);

    @PostConstruct
    private void createBucket() throws MinioException {
        val exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(s3Properties.getBucket()).build());
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(s3Properties.getBucket()).build());
        }
    }

    @Transactional
    public List<UploadRequestResponseDto> getBatchUploadPresignedFormData(
            String userId, BatchUploadRequestDto requestDto) {
        val pendingUploads = requestDto.files().stream()
                .map(r -> {
                    val safeFilename = getSafeFilename(r.filename());
                    val key = "%s/%s".formatted(UUID.randomUUID(), safeFilename);
                    val expiresAt = Instant.now().plus(UPLOAD_EXPIRY_DURATION);

                    return new PendingUploadEntity(key, safeFilename, userId, expiresAt);
                })
                .toList();
        pendingUploadRepository.saveAll(pendingUploads);

        return pendingUploads.stream()
                .map(u -> {
                    val contentType = ImageType.fromFilename(u.getFilename())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Could not resolve ImageType for filename: " + u.getFilename()))
                            .getContentType();
                    val postPolicy = new PostPolicy(
                            s3Properties.getBucket(), u.getExpiresAt().atZone(ZoneOffset.UTC));
                    postPolicy.addContentLengthRangeCondition(1, MAX_CONTENT_LENGTH);
                    postPolicy.addEqualsCondition("x-amz-meta-filename", u.getFilename());
                    postPolicy.addEqualsCondition("x-amz-meta-user-id", u.getUserId());
                    postPolicy.addEqualsCondition("key", u.getKey());
                    postPolicy.addEqualsCondition(HttpHeaders.CONTENT_TYPE, contentType);

                    try {
                        val formData = minioClient.getPresignedPostFormData(postPolicy);
                        return UploadRequestResponseDto.fromFormData(
                                formData, u.getFilename(), u.getUserId(), u.getKey(), contentType);
                    } catch (MinioException e) {
                        log.error("Could not create upload presigned post form data", e);
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private String getSafeFilename(String filename) {
        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');

        val filenameWithoutExtension = lastDotIndex > 0 ? trimmedFilename.substring(0, lastDotIndex) : trimmedFilename;
        val filenameSlug = slugify.slugify(filenameWithoutExtension);
        val safeFilenameWithoutExtension = filenameSlug.isBlank() ? "file" : filenameSlug;

        if (lastDotIndex == -1) return safeFilenameWithoutExtension;
        return safeFilenameWithoutExtension
                + trimmedFilename.substring(lastDotIndex).toLowerCase();
    }

    public boolean isUploaded(String key) {
        if (key == null) return false;

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(s3Properties.getBucket())
                    .object(key)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            // this is thrown for 404 not found
            return false;
        } catch (MinioException e) {
            log.error("An unexpected error occurred for stat object with key {}", key, e);
            return false;
        }
    }

    public void commitUploads(Set<String> keys) {
        if (!keys.isEmpty()) pendingUploadRepository.deleteAllByKeyIn(keys);
    }

    public String getFileUrl(String key) {
        if (key == null) return null;

        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(GET)
                    .bucket(s3Properties.getBucket())
                    .object(key)
                    .expiry(24, TimeUnit.HOURS)
                    // related to https://github.com/minio/minio-java/issues/1692
                    .versionId("dummy")
                    .build());
        } catch (MinioException e) {
            log.error("Could not create GET presigned url for key {}", key, e);
            return null;
        }
    }

    public void deleteAllByKeys(Set<String> keys) {
        if (keys.isEmpty()) return;

        val deleteObjects = keys.stream().map(DeleteRequest.Object::new).toList();
        val deleteResults = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(s3Properties.getBucket())
                .objects(deleteObjects)
                .build());

        for (val deleteResult : deleteResults) {
            try {
                val error = deleteResult.get();
                if (error != null) log.error("Error while removing object with key {} - {}", error.objectName(), error);
            } catch (MinioException e) {
                log.error("Could not retrieve delete error result", e);
            }
        }
    }
}

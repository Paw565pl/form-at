package format.backend.storage.service;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.storage.dto.UploadRequestDto;
import format.backend.storage.dto.UploadRequestResponseDto;
import format.backend.storage.entity.PendingUploadEntity;
import format.backend.storage.exception.InvalidFileExtensionException;
import format.backend.storage.properties.MinioProperties;
import format.backend.storage.repository.PendingUploadRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.StatObjectArgs;
import io.minio.Time;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final PendingUploadRepository pendingUploadRepository;

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10 MB
    private static final Map<String, String> validExtensionsToContentTypeMap = Map.of(
            "png", "image/png", "jpg", "image/jpeg", "jpeg", "image/jpeg", "webp", "image/webp", "avif", "image/avif");

    @PostConstruct
    private void createMinioBucket() {
        try {
            val exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
            if (!exists)
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build());
        } catch (Exception e) {
            log.error("Could not create storage bucket", e);
        }
    }

    @Transactional
    public UploadRequestResponseDto getUploadPresignedFormData(
            KeycloakJwtClaims keycloakJwtClaims, UploadRequestDto requestDto) {
        val fileName = requestDto.fileName().replaceAll("[^A-Za-z0-9._-]", "_").trim();
        val fileExtension = List.of(fileName.split("\\.")).getLast();
        if (!validExtensionsToContentTypeMap.containsKey(fileExtension))
            throw new InvalidFileExtensionException(fileExtension, validExtensionsToContentTypeMap.keySet());

        val key = String.format("%s/%s", UUID.randomUUID(), fileName);
        val contentType = validExtensionsToContentTypeMap.get(fileExtension);

        val pendingUpload = new PendingUploadEntity(key, fileName, contentType, keycloakJwtClaims.sub());
        pendingUploadRepository.save(pendingUpload);

        val postPolicy = new PostPolicy(
                minioProperties.getBucketName(), ZonedDateTime.now(Time.UTC).plusMinutes(10));
        postPolicy.addContentLengthRangeCondition(1, MAX_FILE_SIZE);
        postPolicy.addEqualsCondition("key", key);
        postPolicy.addEqualsCondition("fileName", fileName);
        postPolicy.addEqualsCondition(HttpHeaders.CONTENT_TYPE, contentType);

        try {
            val formData = minioClient.getPresignedPostFormData(postPolicy);
            return UploadRequestResponseDto.fromFormData(formData, fileName, key, contentType);
        } catch (Exception e) {
            log.error("Could not create upload request", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isUploaded(String key) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(key)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            // this is thrown for 404 Not Found
            return false;
        } catch (Exception e) {
            log.error("An unexpected error occurred for stat object with {}", key);
            return false;
        }
    }

    public boolean confirmUpload(String key, String userId) {
        if (key == null) return true;
        if (!isUploaded(key)) return false;

        val pendingUploadOpt = pendingUploadRepository.findByKey(key);
        if (pendingUploadOpt.isEmpty()) return false;

        val pendingUpload = pendingUploadOpt.get();
        if (!pendingUpload.getUserId().equals(userId)) return false;
        if (pendingUpload.getExpiresAt().isBefore(Instant.now())) return false;

        return true;
    }
}

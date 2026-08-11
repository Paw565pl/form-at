package format.backend.upload.application.upload;

import com.github.slugify.Slugify;
import format.backend.auth.UserClaims;
import format.backend.upload.domain.entity.ImageType;
import format.backend.upload.domain.entity.UploadEntity;
import format.backend.upload.domain.exception.UserUploadRateLimitExceededException;
import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.S3Properties;
import format.backend.upload.properties.UploadProperties;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.errors.MinioException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetBatchUploadPresignedFormDataHandler {

    private final Slugify slugify;
    private final MinioClient minioClient;

    private final S3Properties s3Properties;
    private final UploadProperties uploadProperties;
    private final UploadRepository uploadRepository;

    public List<UploadRequestResponseDto> handle(UserClaims userClaims, BatchUploadRequestDto requestDto) {
        val userUploadsCountInCheckWindow = uploadRepository.countAllByUserIdAndCreatedAtAfter(
                userClaims.id(),
                Instant.now().minus(uploadProperties.rateLimit().window()));
        val requestFilesCount = requestDto.files().size();
        if (userUploadsCountInCheckWindow + requestFilesCount
                > uploadProperties.rateLimit().maxUploadsInWindow()) {
            throw new UserUploadRateLimitExceededException();
        }

        val uploadEntities = new ArrayList<UploadEntity>(requestFilesCount);
        val uploadRequestResponseDtos = new ArrayList<UploadRequestResponseDto>(requestFilesCount);

        for (var i = 0; i < requestFilesCount; i++) {
            val filename = createSafeFilename(requestDto.files().get(i).filename());
            val tempKey = createTempKey(userClaims, filename);
            uploadEntities.add(UploadEntity.builder()
                    .tempKey(tempKey)
                    .userId(userClaims.id())
                    .build());

            val contentType = ImageType.fromFilename(filename)
                    .orElseThrow(
                            () -> new IllegalStateException("Could not resolve ImageType for filename: " + filename))
                    .getContentType();
            val postPolicy = new PostPolicy(
                    s3Properties.bucket(),
                    Instant.now()
                            .plus(uploadProperties.expiration().postPolicy())
                            .atZone(ZoneOffset.UTC));
            postPolicy.addContentLengthRangeCondition(
                    1, uploadProperties.maxContentLength().toBytes());
            postPolicy.addEqualsCondition("x-amz-meta-filename", filename);
            postPolicy.addEqualsCondition("x-amz-meta-user-id", userClaims.id());
            postPolicy.addEqualsCondition("key", tempKey);
            postPolicy.addEqualsCondition(HttpHeaders.CONTENT_TYPE, contentType);

            try {
                val formData = minioClient.getPresignedPostFormData(postPolicy);
                uploadRequestResponseDtos.add(UploadRequestResponseDto.builder()
                        .formData(formData)
                        .filename(filename)
                        .userId(userClaims.id())
                        .key(tempKey)
                        .contentType(contentType)
                        .build());
            } catch (MinioException e) {
                log.error("Creating post form data for upload failed.", e);
                throw new RuntimeException(e);
            }
        }

        uploadRepository.saveAll(uploadEntities);
        return Collections.unmodifiableList(uploadRequestResponseDtos);
    }

    private String createSafeFilename(String filename) {
        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');

        val filenameWithoutExtension =
                lastDotIndex == -1 ? trimmedFilename : trimmedFilename.substring(0, lastDotIndex);
        val filenameSlug = slugify.slugify(filenameWithoutExtension);
        val safeFilenameWithoutExtension = filenameSlug.isBlank() ? "file" : filenameSlug;

        if (lastDotIndex == -1) return safeFilenameWithoutExtension;

        val extensionWithDot = trimmedFilename.substring(lastDotIndex).toLowerCase(Locale.ROOT);
        return safeFilenameWithoutExtension + extensionWithDot;
    }

    private String createTempKey(UserClaims userClaims, String filename) {
        return "%s%s/%s/%s"
                .formatted(
                        uploadProperties.retention().tempObjectPrefix(), userClaims.id(), UUID.randomUUID(), filename);
    }
}

package format.backend.newupload.application.uploadrequest;

import com.github.slugify.Slugify;
import format.backend.auth.UserClaims;
import format.backend.newupload.domain.entity.ImageType;
import format.backend.newupload.domain.entity.UploadEntity;
import format.backend.newupload.domain.exception.UserUploadRateLimitExceededException;
import format.backend.newupload.domain.repository.UploadRepository;
import format.backend.newupload.properties.S3Properties;
import format.backend.newupload.properties.UploadProperties;
import io.minio.MinioClient;
import io.minio.PostPolicy;
import io.minio.errors.MinioException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadRequestHandler {

    private final Slugify slugify;
    private final MinioClient minioClient;

    private final S3Properties s3Properties;
    private final UploadProperties uploadProperties;
    private final UploadRepository uploadRepository;

    public List<Map<String, String>> handle(UserClaims userClaims, BatchUploadRequestDto requestDto) {
        val userUploadsCountInCheckWindow = uploadRepository.countAllByUserIdAndCreatedAtAfter(
                userClaims.id(),
                Instant.now().minus(uploadProperties.rateLimit().window()));
        val requestFilesCount = requestDto.files().size();
        val totalRequestedUploads = userUploadsCountInCheckWindow + requestFilesCount;
        if (totalRequestedUploads > uploadProperties.rateLimit().maxUploadsInWindow()) {
            throw new UserUploadRateLimitExceededException();
        }

        val uploadEntities = new ArrayList<UploadEntity>(requestFilesCount);
        val uploadRequestResponseDtos = new ArrayList<Map<String, String>>(requestFilesCount);

        for (var i = 0; i < requestFilesCount; i++) {
            val originalFilename = requestDto.files().get(i).filename();
            val imageType = ImageType.fromFilename(originalFilename)
                    .orElseThrow(() ->
                            new IllegalStateException("Could not resolve ImageType for filename: " + originalFilename));
            val safeFilename = createSafeFilename(originalFilename, imageType.getExtension());
            val key = "%s/%s/%s".formatted(userClaims.id(), UUID.randomUUID(), safeFilename);
            uploadEntities.add(
                    UploadEntity.builder().key(key).userId(userClaims.id()).build());

            val postPolicy = new PostPolicy(
                    s3Properties.bucket(),
                    Instant.now()
                            .plus(uploadProperties.expiration().postPolicy())
                            .atZone(ZoneOffset.UTC));
            postPolicy.addContentLengthRangeCondition(
                    1, uploadProperties.maxSize().toBytes());
            postPolicy.addEqualsCondition("x-amz-meta-filename", safeFilename);
            postPolicy.addEqualsCondition("x-amz-meta-user-id", userClaims.id());
            postPolicy.addEqualsCondition("key", key);
            postPolicy.addEqualsCondition(HttpHeaders.CONTENT_TYPE, imageType.getContentType());

            try {
                uploadRequestResponseDtos.add(minioClient.getPresignedPostFormData(postPolicy));
            } catch (MinioException e) {
                log.warn("Creating post form data for upload failed.", e);
                throw new RuntimeException(e);
            }
        }

        uploadRepository.saveAll(uploadEntities);
        return Collections.unmodifiableList(uploadRequestResponseDtos);
    }

    private String createSafeFilename(String filename, String extension) {
        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');

        val filenameWithoutExtension = lastDotIndex > 0 ? trimmedFilename.substring(0, lastDotIndex) : trimmedFilename;
        val filenameSlug = slugify.slugify(filenameWithoutExtension);
        val safeFilenameWithoutExtension = filenameSlug.isBlank() ? "file" : filenameSlug;

        return "%s.%s".formatted(safeFilenameWithoutExtension, extension);
    }
}

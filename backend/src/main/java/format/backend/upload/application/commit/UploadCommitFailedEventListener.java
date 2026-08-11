package format.backend.upload.application.commit;

import format.backend.upload.domain.entity.UploadEntity;
import format.backend.upload.domain.entity.UploadStatus;
import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.S3Properties;
import format.backend.upload.properties.UploadProperties;
import io.minio.CopyObjectArgs;
import io.minio.MinioClient;
import io.minio.SourceObject;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class UploadCommitFailedEventListener {

    private final MinioClient minioClient;

    private final S3Properties s3Properties;
    private final UploadProperties uploadProperties;
    private final UploadRepository uploadRepository;

    @ApplicationModuleListener
    void on(UploadCommitFailedEvent event) throws MinioException {
        log.debug(
                "Retrying failed upload commit. sourceKey={}, destinationKey={}",
                event.sourceKey(),
                event.destinationKey());

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(s3Properties.bucket())
                    .object(event.destinationKey())
                    .source(SourceObject.builder()
                            .bucket(s3Properties.bucket())
                            .object(event.sourceKey())
                            .build())
                    .build());
            uploadRepository.updateStatusByTempKeyAndStatus(
                    event.sourceKey(), UploadStatus.COMMITTING, UploadStatus.COMMITTED);

            log.debug(
                    "Upload commit retry succeeded. Object was successfully copied. sourceKey={}, destinationKey={}",
                    event.sourceKey(),
                    event.destinationKey());
        } catch (Exception e) {
            if (e instanceof ErrorResponseException errorResponseException) {
                try (val response = errorResponseException.response()) {
                    if (response.code() == HttpStatus.NOT_FOUND.value()) {
                        uploadRepository.updateStatusByTempKeyAndStatus(
                                event.sourceKey(), UploadStatus.COMMITTING, UploadStatus.FAILED);
                        log.warn(
                                "Upload commit retry failed. Object was not found. sourceKey={}, destinationKey={}",
                                event.sourceKey(),
                                event.destinationKey());

                        return;
                    }
                }
            }

            val uploadEntity = uploadRepository
                    .findByTempKeyAndStatus(event.sourceKey(), UploadStatus.COMMITTING)
                    .orElse(null);

            // TODO: byc moze usunac sprawdzenie Instant.now()
            //                                .isAfter(u.getCreatedAt()
            //                                        .plus(uploadProperties.commit().maxRetryWindow()))
            switch (uploadEntity) {
                case null ->
                    log.warn(
                            "Upload commit retry failed. No corresponding upload entity found. sourceKey={}, destinationKey={}",
                            event.sourceKey(),
                            event.destinationKey(),
                            e);
                case UploadEntity u
                when u.getCreatedAt() != null
                        && Instant.now()
                                .isAfter(u.getCreatedAt()
                                        .plus(uploadProperties.commit().maxRetryWindow())) -> {
                    uploadRepository.updateStatusByTempKeyAndStatus(
                            event.sourceKey(), UploadStatus.COMMITTING, UploadStatus.FAILED);
                    log.warn(
                            "Upload commit retry failed. Marking upload as failed. sourceKey={}, destinationKey={}",
                            event.sourceKey(),
                            event.destinationKey(),
                            e);
                }
                case UploadEntity _ -> {
                    log.warn(
                            "Upload commit retry failed. Commit will be retried. sourceKey={}, destinationKey={}",
                            event.sourceKey(),
                            event.destinationKey(),
                            e);
                    throw e;
                }
            }
        }
    }
}

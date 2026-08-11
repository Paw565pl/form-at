package format.backend.upload.application.commit;

import format.backend.upload.application.resolve.ResolveDestinationKeyHandler;
import format.backend.upload.domain.entity.UploadStatus;
import format.backend.upload.domain.repository.UploadRepository;
import format.backend.upload.properties.S3Properties;
import format.backend.upload.properties.UploadProperties;
import io.minio.CopyObjectArgs;
import io.minio.MinioClient;
import io.minio.SourceObject;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class CommitUploadsHandler {

    private final MinioClient minioClient;
    private final ApplicationEventPublisher eventPublisher;
    private final AsyncTaskExecutor asyncTaskExecutor;
    private final TransactionTemplate transactionTemplate;

    private final S3Properties s3Properties;
    private final UploadRepository uploadRepository;
    private final ResolveDestinationKeyHandler resolveDestinationKeyHandler;

    private final Semaphore copyObjectSemaphore;

    public CommitUploadsHandler(
            MinioClient minioClient,
            ApplicationEventPublisher eventPublisher,
            @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor,
            PlatformTransactionManager transactionManager,
            S3Properties s3Properties,
            UploadRepository uploadRepository,
            ResolveDestinationKeyHandler resolveDestinationKeyHandler,
            UploadProperties uploadProperties) {
        this.minioClient = minioClient;
        this.eventPublisher = eventPublisher;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.s3Properties = s3Properties;
        this.uploadRepository = uploadRepository;
        this.resolveDestinationKeyHandler = resolveDestinationKeyHandler;
        this.copyObjectSemaphore = new Semaphore(uploadProperties.concurrency().maxCopyOperations(), true);
    }

    public void handle(Collection<String> tempKeys) {
        if (tempKeys.isEmpty()) return;
        uploadRepository.updateStatusByTempKeyInAndStatus(tempKeys, UploadStatus.PENDING, UploadStatus.COMMITTING);

        val copyObjectFutures = new ArrayList<CompletableFuture<CopyResult>>(tempKeys.size());
        for (val tempKey : tempKeys) {
            val destinationKey = resolveDestinationKeyHandler.handle(tempKey).orElse(null);
            if (destinationKey == null) continue;

            copyObjectFutures.add(asyncTaskExecutor.submitCompletable(() -> copyObject(tempKey, destinationKey)));
        }

        val resultsByStatus = copyObjectFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.groupingBy(CopyResult::status));

        val succeeded = resultsByStatus.getOrDefault(CopyStatus.SUCCESS, List.of());
        val terminal = resultsByStatus.getOrDefault(CopyStatus.TERMINAL_FAILURE, List.of());
        val retryable = resultsByStatus.getOrDefault(CopyStatus.RETRYABLE_FAILURE, List.of());

        // TODO: this code block probably should be a transaction
        if (!succeeded.isEmpty()) {
            val succeededKeys = succeeded.stream().map(CopyResult::sourceKey).toList();
            uploadRepository.updateStatusByTempKeyInAndStatus(
                    succeededKeys, UploadStatus.COMMITTING, UploadStatus.COMMITTED);
        }

        if (!terminal.isEmpty()) {
            val terminalKeys = terminal.stream().map(CopyResult::sourceKey).toList();
            uploadRepository.updateStatusByTempKeyInAndStatus(
                    terminalKeys, UploadStatus.COMMITTING, UploadStatus.FAILED);
        }

        if (!retryable.isEmpty()) {
            transactionTemplate.executeWithoutResult(
                    _ -> retryable.forEach(result -> eventPublisher.publishEvent(UploadCommitFailedEvent.builder()
                            .sourceKey(result.sourceKey())
                            .destinationKey(result.destinationKey())
                            .build())));
        }
        //
    }

    private CopyResult copyObject(String sourceKey, String destinationKey) {
        try {
            copyObjectSemaphore.acquire();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return CopyResult.builder()
                    .sourceKey(sourceKey)
                    .destinationKey(destinationKey)
                    .status(CopyStatus.RETRYABLE_FAILURE)
                    .build();
        }

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(s3Properties.bucket())
                    .object(destinationKey)
                    .source(SourceObject.builder()
                            .bucket(s3Properties.bucket())
                            .object(sourceKey)
                            .build())
                    .build());
            return CopyResult.builder()
                    .sourceKey(sourceKey)
                    .destinationKey(destinationKey)
                    .status(CopyStatus.SUCCESS)
                    .build();
        } catch (MinioException e) {
            if (e instanceof ErrorResponseException errorResponseException) {
                try (val response = errorResponseException.response()) {
                    if (response.code() == HttpStatus.NOT_FOUND.value()) {
                        log.warn(
                                "Upload commit failed. Object was not found. sourceKey={}, destinationKey={}",
                                sourceKey,
                                destinationKey);
                        return CopyResult.builder()
                                .sourceKey(sourceKey)
                                .destinationKey(destinationKey)
                                .status(CopyStatus.TERMINAL_FAILURE)
                                .build();
                    }
                }
            }

            log.warn("Upload commit failed. sourceKey={}, destinationKey={}", sourceKey, destinationKey, e);
            return CopyResult.builder()
                    .sourceKey(sourceKey)
                    .destinationKey(destinationKey)
                    .status(CopyStatus.RETRYABLE_FAILURE)
                    .build();
        } catch (Exception e) {
            log.error(
                    "Upload commit failed unexpectedly. sourceKey={}, destinationKey={}", sourceKey, destinationKey, e);
            return CopyResult.builder()
                    .sourceKey(sourceKey)
                    .destinationKey(destinationKey)
                    .status(CopyStatus.RETRYABLE_FAILURE)
                    .build();
        } finally {
            copyObjectSemaphore.release();
        }
    }

    private enum CopyStatus {
        SUCCESS,
        RETRYABLE_FAILURE,
        TERMINAL_FAILURE
    }

    @Builder
    private record CopyResult(String sourceKey, String destinationKey, CopyStatus status) {}
}

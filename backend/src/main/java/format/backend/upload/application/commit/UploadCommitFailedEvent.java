package format.backend.upload.application.commit;

import lombok.Builder;

@Builder
record UploadCommitFailedEvent(String sourceKey, String destinationKey) {}

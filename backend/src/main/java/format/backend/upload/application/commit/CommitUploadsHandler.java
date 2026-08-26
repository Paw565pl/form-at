package format.backend.upload.application.commit;

import format.backend.auth.UserClaims;
import format.backend.upload.domain.entity.UploadStatus;
import format.backend.upload.domain.exception.UploadCommitFailedException;
import format.backend.upload.domain.repository.UploadRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommitUploadsHandler {

    private final UploadRepository uploadRepository;

    public void handle(Set<String> keys, UserClaims userClaims) {
        if (keys.isEmpty()) return;

        val updatedCount = uploadRepository.updateStatusByKeyInAndUserIdAndStatus(
                keys, userClaims.id(), UploadStatus.PENDING, UploadStatus.COMPLETED);
        if (keys.size() != updatedCount) throw new UploadCommitFailedException();
    }
}

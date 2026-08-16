package format.backend.newupload.application.commit;

import format.backend.auth.UserClaims;
import format.backend.newupload.domain.entity.UploadStatus;
import format.backend.newupload.domain.repository.UploadRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommitUploadsHandler {

    private final UploadRepository uploadRepository;

    public long handle(Set<String> keys, UserClaims userClaims) {
        if (keys.isEmpty()) return 0;
        return uploadRepository.updateStatusByKeyInAndUserIdAndStatus(
                keys, userClaims.id(), UploadStatus.PENDING, UploadStatus.COMPLETED);
    }
}

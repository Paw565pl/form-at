package format.backend.newupload;

import format.backend.auth.UserClaims;
import format.backend.newupload.application.commit.CommitUploadsHandler;
import format.backend.newupload.application.delete.DeleteUploadsHandler;
import format.backend.newupload.application.presignget.PresignGetUrlHandler;
import format.backend.newupload.application.validate.GetInvalidUploadKeysHandler;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadFacade {

    private final PresignGetUrlHandler presignGetUrlHandler;
    private final GetInvalidUploadKeysHandler getInvalidUploadKeysHandler;
    private final CommitUploadsHandler commitUploadsHandler;
    private final DeleteUploadsHandler deleteUploadsHandler;

    public Optional<String> presignGetUrl(@Nullable String key) {
        return presignGetUrlHandler.handle(key);
    }

    public Set<String> getInvalidKeys(Set<String> keys, UserClaims userClaims) {
        return getInvalidUploadKeysHandler.handle(keys, userClaims);
    }

    /// mark status of PENDING uploads as COMPLETED
    public void commit(Set<String> keys, UserClaims userClaims) {
        commitUploadsHandler.handle(keys, userClaims);
    }

    public long deleteAll(Set<String> keys) {
        return deleteUploadsHandler.handle(keys);
    }
}

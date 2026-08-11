package format.backend.upload;

import format.backend.auth.UserClaims;
import format.backend.upload.application.commit.CommitUploadsHandler;
import format.backend.upload.application.delete.DeleteUploadsHandler;
import format.backend.upload.application.presignurl.CreatePresignedFileUrlHandler;
import format.backend.upload.application.resolve.ResolveDestinationKeyHandler;
import format.backend.upload.application.validate.GetInvalidUploadKeysHandler;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadFacade {

    private final CommitUploadsHandler commitHandler;
    private final CreatePresignedFileUrlHandler createPresignedFileUrlHandler;
    private final DeleteUploadsHandler deleteHandler;
    private final ResolveDestinationKeyHandler resolveDestinationKeyHandler;
    private final GetInvalidUploadKeysHandler getInvalidUploadKeysHandler;

    public void commit(Collection<String> tempKeys) {
        commitHandler.handle(tempKeys);
    }

    public Optional<String> createPresignedFileUrl(@Nullable String key) {
        return createPresignedFileUrlHandler.handle(key);
    }

    public boolean delete(Collection<String> keys) {
        return deleteHandler.handle(keys);
    }

    public Optional<String> resolveDestinationKey(@Nullable String tempKey) {
        return resolveDestinationKeyHandler.handle(tempKey);
    }

    public Set<String> getInvalidKeys(Set<String> tempKeys, UserClaims userClaims) {
        return getInvalidUploadKeysHandler.handle(tempKeys, userClaims);
    }
}

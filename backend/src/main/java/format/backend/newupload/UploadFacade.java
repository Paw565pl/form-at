package format.backend.newupload;

import format.backend.auth.UserClaims;
import format.backend.newupload.application.presignedget.CreatePresignedGetUrlHandler;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadFacade {

    private final CreatePresignedGetUrlHandler createPresignedGetUrlHandler;

    public Optional<String> createPresignedGetUrl(@Nullable String key) {
        return createPresignedGetUrlHandler.handle(key);
    }

    /// marks status of upload keys as COMPLETED
    public void commit(Set<String> keys, UserClaims userClaims) {
        throw new UnsupportedOperationException();
    }

    /// validates input keys and returns invalid ones
    public Set<String> getInvalidKeys(Set<String> keys, UserClaims userClaims) {
        throw new UnsupportedOperationException();
    }

    public long deleteAll(Set<String> keys) {
        throw new UnsupportedOperationException();
    }
}

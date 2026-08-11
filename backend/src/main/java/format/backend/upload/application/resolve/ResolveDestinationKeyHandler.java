package format.backend.upload.application.resolve;

import format.backend.upload.properties.UploadProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResolveDestinationKeyHandler {

    private final UploadProperties uploadProperties;

    public Optional<String> handle(@Nullable String tempKey) {
        if (tempKey == null) return Optional.empty();
        val tempPrefix = uploadProperties.retention().tempObjectPrefix();

        if (!tempKey.startsWith(tempPrefix)) return Optional.empty();
        return Optional.of(tempKey.substring(tempPrefix.length()));
    }
}

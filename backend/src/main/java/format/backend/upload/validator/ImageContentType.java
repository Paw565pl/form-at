package format.backend.upload.validator;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageContentType {
    AVIF("image/avif");

    private final String value;

    public static Optional<ImageContentType> from(String contentType) {
        if (!Objects.equals(contentType.toLowerCase(), AVIF.value)) {
            return Optional.empty();
        }

        return Optional.of(AVIF);
    }
}

package format.backend.upload.validator;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum ImageExtension {
    AVIF("avif");

    private final String value;

    public static Optional<ImageExtension> fromExtension(String extension) {
        if (!Objects.equals(extension.toLowerCase(), AVIF.value)) {
            return Optional.empty();
        }

        return Optional.of(AVIF);
    }

    public static Optional<ImageExtension> fromFilename(String filename) {
        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');
        if (lastDotIndex == -1) return Optional.empty();

        val extension = trimmedFilename.substring(lastDotIndex + 1);
        return fromExtension(extension);
    }
}

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

    public static Optional<ImageExtension> from(String extension) {
        if (!Objects.equals(extension.toLowerCase(), AVIF.value)) {
            return Optional.empty();
        }

        return Optional.of(AVIF);
    }

    public static Optional<ImageExtension> fromFilename(String filename) {
        val lowerCaseFilename = filename.trim().toLowerCase();
        val lastDotIndex = lowerCaseFilename.lastIndexOf('.');
        val extension = lastDotIndex == -1 ? lowerCaseFilename : lowerCaseFilename.substring(lastDotIndex + 1);

        return from(extension);
    }
}

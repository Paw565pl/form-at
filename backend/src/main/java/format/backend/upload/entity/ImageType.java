package format.backend.upload.entity;

import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    AVIF("avif", "image/avif");

    private final String extension;
    private final String contentType;

    public static Optional<ImageType> fromExtension(String extension) {
        if (!Objects.equals(extension.toLowerCase(), AVIF.extension)) {
            return Optional.empty();
        }

        return Optional.of(AVIF);
    }

    public static Optional<ImageType> fromFilename(String filename) {
        if (filename == null) return Optional.empty();

        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');
        if (lastDotIndex == -1) return Optional.empty();

        val extension = trimmedFilename.substring(lastDotIndex + 1);
        return fromExtension(extension);
    }
}

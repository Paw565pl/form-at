package format.backend.upload.domain.entity;

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
        if (AVIF.extension.equalsIgnoreCase(extension)) return Optional.of(AVIF);
        return Optional.empty();
    }

    public static Optional<ImageType> fromFilename(String filename) {
        val trimmedFilename = filename.trim();
        val lastDotIndex = trimmedFilename.lastIndexOf('.');
        if (lastDotIndex <= 0) return Optional.empty();

        val extension = trimmedFilename.substring(lastDotIndex + 1);
        return fromExtension(extension);
    }
}

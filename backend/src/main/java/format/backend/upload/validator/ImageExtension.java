package format.backend.upload.validator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor
public enum ImageExtension {
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    WEBP("webp", "image/webp"),
    AVIF("avif", "image/avif");

    private final String extensionValue;
    private final String contentType;

    public static Optional<ImageExtension> fromExtensionValue(String extensionValue) {
        if (extensionValue == null) return Optional.empty();

        for (val imageExtension : values()) {
            if (imageExtension.getExtensionValue().equalsIgnoreCase(extensionValue)) {
                return Optional.of(imageExtension);
            }
        }

        return Optional.empty();
    }
}

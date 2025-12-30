package format.backend.upload.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    private static final Map<String, ImageExtension> extensionValueToImageExtensionMap = Arrays.stream(values())
            .collect(Collectors.toMap(l -> l.getExtensionValue().toLowerCase(), l -> l));

    public static Optional<ImageExtension> fromStringValue(String value) {
        return Optional.ofNullable(extensionValueToImageExtensionMap.get(value.toLowerCase()));
    }
}

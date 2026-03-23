package format.backend.upload.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum ImageExtension {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    WEBP("webp"),
    AVIF("avif");

    private final String value;

    private static final Map<String, ImageExtension> valueToImageExtensionMap =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(i -> i.value, Function.identity()));

    public static Optional<ImageExtension> from(String extension) {
        return Optional.ofNullable(valueToImageExtensionMap.get(extension.toLowerCase()));
    }

    public static Optional<ImageExtension> fromFilename(String filename) {
        val lowerCaseFilename = filename.trim().toLowerCase();
        val lastDotIndex = lowerCaseFilename.lastIndexOf('.');
        val extension = lastDotIndex == -1 ? lowerCaseFilename : lowerCaseFilename.substring(lastDotIndex + 1);

        return from(extension);
    }
}

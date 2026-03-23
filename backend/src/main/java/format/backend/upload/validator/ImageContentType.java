package format.backend.upload.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageContentType {
    PNG("image/png"),
    JPG("image/jpg"),
    JPEG("image/jpeg"),
    WEBP("image/webp"),
    AVIF("image/avif");

    private final String value;

    private static final Map<String, ImageContentType> valueToImageContentTypeMap =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(i -> i.value, Function.identity()));

    public static Optional<ImageContentType> from(String contentType) {
        return Optional.ofNullable(valueToImageContentTypeMap.get(contentType.toLowerCase()));
    }
}

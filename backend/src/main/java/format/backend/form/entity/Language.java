package format.backend.form.entity;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    EN("english"),
    PL("none");

    private final String value;

    private static final Map<String, Language> valueToLanguageMap =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(l -> l.value, Function.identity()));

    public static Optional<Language> from(String language) {
        return Optional.ofNullable(valueToLanguageMap.get(language.toLowerCase(Locale.ROOT)));
    }
}

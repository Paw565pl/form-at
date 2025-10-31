package format.backend.form.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    EN("english"),
    PL("none");

    private final String mongoValue;

    private static final Map<String, Language> mongoNameToLanguageMap =
            Arrays.stream(values()).collect(Collectors.toMap(l -> l.mongoValue.toLowerCase(), l -> l));

    public static Optional<Language> fromMongoValue(String mongoName) {
        return Optional.ofNullable(mongoNameToLanguageMap.get(mongoName.toLowerCase()));
    }
}

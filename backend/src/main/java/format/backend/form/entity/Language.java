package format.backend.form.entity;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@Getter
@AllArgsConstructor
public enum Language {
    EN("english"),
    PL("none");

    private final String value;

    private static final Language[] VALUES = values();

    public static Optional<Language> fromValue(String value) {
        if (value == null) return Optional.empty();

        for (val language : VALUES) {
            if (language.getValue().equalsIgnoreCase(value)) {
                return Optional.of(language);
            }
        }

        return Optional.empty();
    }
}

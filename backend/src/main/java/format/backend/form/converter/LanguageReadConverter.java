package format.backend.form.converter;

import format.backend.form.entity.Language;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class LanguageReadConverter implements Converter<@NonNull String, @NonNull Language> {

    @Override
    public Language convert(@NonNull String source) {
        return Language.from(source)
                .orElseThrow(() -> new IllegalArgumentException("Invalid language mongo value: " + source));
    }
}

package format.backend.comment_rating.converter;

import format.backend.comment_rating.entity.RatingType;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RatingTypeConverter implements Converter<@NonNull Integer, @NonNull RatingType> {

    public RatingType convert(Integer source) {
        return RatingType.fromValue(source).orElse(null);
    }
}

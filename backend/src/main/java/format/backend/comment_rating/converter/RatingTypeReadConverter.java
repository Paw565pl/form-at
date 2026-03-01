package format.backend.comment_rating.converter;

import format.backend.comment_rating.entity.RatingType;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RatingTypeReadConverter implements Converter<@NonNull Integer, @NonNull RatingType> {

    @Override
    public RatingType convert(@NonNull Integer source) {
        return RatingType.fromValue(source)
                .orElseThrow(() -> new IllegalStateException("Invalid rating type: " + source));
    }
}

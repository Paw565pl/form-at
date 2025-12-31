package format.backend.comment.converter;

import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment_rating.entity.RatingType;
import java.time.Instant;
import org.bson.Document;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class CommentResponseDtoConverter implements Converter<@NonNull Document, CommentResponseDto> {

    public CommentResponseDto convert(Document source) {
        Integer userRatingRaw = source.getInteger("userRating");

        return new CommentResponseDto(
                source.getString("_id"),
                source.getString("authorName"),
                source.getString("content"),
                source.getLong("ratingScore"),
                userRatingRaw != null
                        ? RatingType.fromValue(userRatingRaw).orElse(null)
                        : null,
                source.get("createdAt", Instant.class),
                source.get("updatedAt", Instant.class)
        );
    }
}

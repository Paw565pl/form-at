package format.backend.comment.converter;

import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment_rating.entity.RatingType;
import java.time.Instant;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class CommentResponseDtoConverter implements Converter<@NonNull Document, CommentResponseDto> {

    public CommentResponseDto convert(Document source) {
        Integer userRatingRaw = source.getInteger("userRating");

        ObjectId id = source.getObjectId("_id");
        if (id == null) {
            throw new IllegalStateException("CommentId cannot be null");
        }

        String idString = id.toHexString();

        Date createdAtDate = source.getDate("createdAt");
        Date updatedAtDate = source.getDate("updatedAt");

        return new CommentResponseDto(
                idString,
                source.getString("authorName"),
                source.getString("content"),
                source.getLong("ratingScore"),
                userRatingRaw != null ? RatingType.fromValue(userRatingRaw).orElse(null) : null,
                createdAtDate.toInstant(),
                updatedAtDate.toInstant());
    }
}

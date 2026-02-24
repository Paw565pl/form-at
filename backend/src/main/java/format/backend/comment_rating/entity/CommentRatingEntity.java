package format.backend.comment_rating.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(
        def = "{'commentId': 1, 'authorId': 1}",
        unique = true,
        partialFilter = "{'authorId': {'$type': 'string'}}")
@Document(collection = "commentRatings")
public class CommentRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "commentId")
    @NonNull private String commentId;

    @Field(name = "authorId")
    @Nullable private String authorId;

    @Field(name = "type", targetType = FieldType.INT32)
    private Integer type;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("updatedAt")
    private Instant updatedAt;

    @Version
    @Field(name = "version")
    private Long version;
}

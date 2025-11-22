package format.backend.comment.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(
        name = "idx_commentId_authorId_unique",
        def = "{'commentId': 1, 'authorId': 1}",
        unique = true
)
@Document(collection = "comment_ratings")
public class CommentRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "commentId", targetType = FieldType.OBJECT_ID)
    @NonNull
    private CommentEntity comment;

    @Field(name = "type")
    @NonNull
    private RatingType type;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("updatedAt")
    private Instant updatedAt;
}

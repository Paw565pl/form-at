package format.backend.commentRating.entity;

import format.backend.comment.entity.CommentEntity;
import format.backend.commentRating.entity.RatingType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;
import org.springframework.lang.NonNull;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(name = "idx_commentId_authorId_unique", def = "{'commentId': 1, 'authorId': 1}", unique = true)
@Document(collection = "comment_ratings")
public class CommentRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "commentId", targetType = FieldType.OBJECT_ID)
    @NonNull private CommentEntity comment;

    @Field(name = "type")
    @NonNull private RatingType type;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("updatedAt")
    private Instant updatedAt;
}

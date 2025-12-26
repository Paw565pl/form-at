package format.backend.comment_rating.entity;

import format.backend.auth.entity.UserEntity;
import format.backend.comment.entity.CommentEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;
import org.jspecify.annotations.NonNull;

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
    @Field(name = "commentId")
    @NonNull private CommentEntity comment;

    @DocumentReference(lazy = true)
    @Field(name = "authorId")
    @NonNull private UserEntity author;

    @Field(name = "type", targetType = FieldType.INT32)
    private int type;

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

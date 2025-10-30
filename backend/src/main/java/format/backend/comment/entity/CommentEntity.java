package format.backend.comment.entity;

import format.backend.auth.entity.UserEntity;
import java.time.Instant;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "comments")
public class CommentEntity {

    @MongoId(targetType = FieldType.OBJECT_ID)
    @Field(name = "_id")
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "author")
    private UserEntity author;

    @NonNull @Field(name = "content")
    private String content;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;
}

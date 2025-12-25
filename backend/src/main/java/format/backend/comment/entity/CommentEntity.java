package format.backend.comment.entity;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "comments")
public class CommentEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "authorId")
    @Nullable private UserEntity author;

    @DocumentReference(lazy = true)
    @Field(name = "formId")
    @NonNull private FormEntity form;

    @Field(name = "content")
    @NonNull private String content;

    @Field(name = "ratingCount")
    @Setter(AccessLevel.NONE)
    private @NonNull Long ratingCount = 0L;

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

package format.backend.comment.entity;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(def = "{'formId': 1, 'updatedAt': -1, '_id': 1}")
@Document(collection = "comments")
public class CommentEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed
    @DocumentReference(lazy = true)
    @Field(name = "authorId")
    private @Nullable UserEntity author;

    @DocumentReference(lazy = true)
    @Field(name = "formId")
    private @NonNull FormEntity form;

    @Field(name = "content")
    private @NonNull String content;

    @Field(name = "ratingScore")
    @Setter(AccessLevel.NONE)
    private @NonNull Long ratingScore = 0L;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;

    @Version
    @Field(name = "version")
    private Long version;
}

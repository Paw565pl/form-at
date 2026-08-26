package format.backend.formcomment.domain.entity;

import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@CompoundIndex(
        def = "{'commentId': 1, 'authorId': 1}",
        partialFilter = "{'authorId': {'$type': 'string'}}",
        unique = true)
@Document(collection = "formCommentRatings")
public final class FormCommentRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Indexed
    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private final String formId;

    @Indexed
    @Field(name = "commentId", targetType = FieldType.OBJECT_ID)
    private final String commentId;

    @Field(name = "authorId")
    private final @Nullable String authorId;

    @Setter
    @Field(name = "type")
    private FormCommentRatingType type;

    @CreatedDate
    @Field(name = "createdAt")
    private final @Nullable Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private final @Nullable Instant updatedAt;

    @Version
    @Field(name = "version")
    private final long version;

    @Builder
    public FormCommentRatingEntity(String formId, String commentId, String authorId, FormCommentRatingType type) {
        this.id = null;
        this.formId = Objects.requireNonNull(formId);
        this.commentId = Objects.requireNonNull(commentId);
        this.authorId = Objects.requireNonNull(authorId);
        this.type = Objects.requireNonNull(type);
        this.createdAt = null;
        this.updatedAt = null;
        this.version = 0;
    }
}

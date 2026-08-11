package format.backend.formcomment.domain.entity;

import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
@CompoundIndex(def = "{'formId': 1, '_id': -1}")
@Document(collection = "formComments")
public final class FormCommentEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private final String formId;

    @Indexed(partialFilter = "{'authorId': {'$type': 'string'}}")
    @Field(name = "authorId")
    private final @Nullable String authorId;

    @Field(name = "content")
    private String content;

    @Field(name = "ratingScore")
    private final long ratingScore;

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
    public FormCommentEntity(String formId, String authorId, String content) {
        this.id = null;
        this.formId = Objects.requireNonNull(formId);
        this.authorId = Objects.requireNonNull(authorId);
        setContent(Objects.requireNonNull(content));
        this.ratingScore = 0;
        this.createdAt = null;
        this.updatedAt = null;
        this.version = 0;
    }

    public void setContent(String content) {
        this.content = content.trim();
    }
}

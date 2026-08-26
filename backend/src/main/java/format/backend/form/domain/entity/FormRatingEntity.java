package format.backend.form.domain.entity;

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
@CompoundIndex(def = "{'formId': 1, 'authorId': 1}", unique = true, partialFilter = "{'authorId': {'$type': 'string'}}")
@Document(collection = "formRatings")
public final class FormRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Indexed
    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private final String formId;

    @Field(name = "authorId")
    private final @Nullable String authorId;

    @Field(name = "value")
    private int value;

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
    public FormRatingEntity(String formId, String authorId, Integer value) {
        this.id = null;
        this.formId = Objects.requireNonNull(formId);
        this.authorId = Objects.requireNonNull(authorId);
        setValue(Objects.requireNonNull(value));
        this.createdAt = null;
        this.updatedAt = null;
        this.version = 0;
    }

    public void setValue(int value) {
        this.value = Math.clamp(value, 1, 5);
    }
}

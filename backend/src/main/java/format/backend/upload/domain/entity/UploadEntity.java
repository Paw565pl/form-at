package format.backend.upload.domain.entity;

import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@CompoundIndex(def = "{'userId': 1, 'createdAt': 1}")
@Document(collection = "uploads")
public final class UploadEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Indexed(unique = true)
    @Field(name = "tempKey")
    private final String tempKey;

    @Field(name = "userId")
    private final String userId;

    @Field(name = "status")
    private final UploadStatus status;

    @Indexed
    @CreatedDate
    @Field(name = "createdAt")
    private final @Nullable Instant createdAt;

    @Builder
    public UploadEntity(String tempKey, String userId) {
        this.id = null;
        this.tempKey = Objects.requireNonNull(tempKey);
        this.userId = Objects.requireNonNull(userId);
        this.status = UploadStatus.PENDING;
        this.createdAt = null;
    }
}

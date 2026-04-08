package format.backend.upload.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "pendingUploads")
public class PendingUploadEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed
    @Field(name = "key")
    private @NonNull String key;

    @Field(name = "filename")
    private @NonNull String filename;

    @Field(name = "userId")
    private @NonNull String userId;

    @Indexed
    @Field(name = "expiresAt")
    private @NonNull Instant expiresAt;

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;
}

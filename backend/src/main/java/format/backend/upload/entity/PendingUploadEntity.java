package format.backend.upload.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;

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
    @NonNull private String key;

    @Field(name = "fileName")
    @NonNull private String fileName;

    @Field(name = "contentType")
    @NonNull private String contentType;

    @Field(name = "userId")
    @NonNull private String userId;

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;

    @Indexed
    @Field(name = "expiresAt")
    @NonNull private Instant expiresAt;
}

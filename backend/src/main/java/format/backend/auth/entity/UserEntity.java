package format.backend.auth.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "users")
public class UserEntity {

    @MongoId
    @Field(name = "_id")
    @NonNull private String id;

    @Field(name = "username")
    @NonNull private String username;

    @Field(name = "email")
    @NonNull private String email;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt = Instant.now();
}

package format.backend.auth.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "users")
public class UserEntity {

    @MongoId
    @Field(name = "_id")
    private @NonNull String id;

    @Field(name = "username")
    private @NonNull String username;

    @Field(name = "email")
    private @NonNull String email;

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Field(name = "updatedAt")
    private Instant updatedAt = Instant.now();
}

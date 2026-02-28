package format.backend.auth.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "users")
public class UserEntity {

    @MongoId
    @Field(name = "_id")
    private @NonNull String id;

    @Indexed
    @Field(name = "username")
    private @NonNull String username;

    @Field(name = "email")
    private @NonNull String email;

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private Instant updatedAt;

    public UserEntity(@NonNull String id, @NonNull String username, @NonNull String email) {
        val now = Instant.now();

        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = now;
        this.updatedAt = now;
    }
}

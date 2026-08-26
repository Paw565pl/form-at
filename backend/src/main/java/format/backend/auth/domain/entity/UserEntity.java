package format.backend.auth.domain.entity;

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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Document(collection = "users")
public final class UserEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.STRING)
    private final String id;

    @Indexed(unique = true)
    @Field(name = "username")
    private final String username;

    @Field(name = "email")
    private final String email;

    @CreatedDate
    @Field(name = "createdAt")
    private final @Nullable Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private final @Nullable Instant updatedAt;

    @Builder
    public UserEntity(String id, String username, String email) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.createdAt = null;
        this.updatedAt = null;
    }
}

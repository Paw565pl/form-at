package format.backend.form_rating.entity;


import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(name = "idx_formId_authorId_unique", def = "{'formId': 1, 'authorId': 1}", unique = true)
@Document(collection = "form_ratings")
public class FormRatingEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @DocumentReference(lazy = true)
    @Field(name = "formId")
    @NonNull private FormEntity comment;

    @DocumentReference(lazy = true)
    @Field(name = "authorId")
    @NonNull
    private UserEntity author;

    @Field(name = "rating", targetType = FieldType.INT32)
    private Integer rating;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("updatedAt")
    private Instant updatedAt;

    @Version
    @Field(name = "version")
    private Long version;
}

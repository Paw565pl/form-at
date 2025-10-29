package format.backend.form.entity;

import format.backend.auth.entity.UserEntity;
import format.backend.submission.entity.SubmissionEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "forms")
public class FormEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "name")
    @TextIndexed(weight = 2.0F)
    @NonNull private String name;

    @Field(name = "slug")
    @Indexed(unique = true)
    @NonNull private String slug;

    @Field(name = "description")
    @TextIndexed(weight = 1.0F)
    @Nullable private String description;

    @Field(name = "status")
    @Indexed
    @NonNull private FormStatus status;

    @Field(name = "passwordHash")
    @Nullable private String passwordHash;

    @Field(name = "shuffleVariant")
    @Nullable private FormShuffleVariant shuffleVariant;

    @Field(name = "thanksMessage")
    @Nullable private String thanksMessage;

    @Field(name = "estimatedDuration")
    @NonNull private Duration estimatedDuration;

    @Field(name = "thumbnailKey")
    @Nullable private String thumbnailKey;

    @Field(name = "allowsQuestionsPreview")
    @NonNull private Boolean allowsQuestionsPreview;

    @Field(name = "allowsGuestSubmissions")
    @NonNull private Boolean allowsGuestSubmissions;

    @Field(name = "saveSubmissions")
    @NonNull private Boolean saveSubmissions;

    @Field(name = "submissionsCount")
    @NonNull private Long submissionsCount = 0L;

    @Field(name = "authorId")
    @DocumentReference(lazy = true)
    @NonNull private UserEntity author;

    @Field(name = "questions")
    @NonNull private List<QuestionEntity> questions = new ArrayList<>();

    @ReadOnlyProperty
    @DocumentReference(lazy = true, lookup = "{'formId':?#{#self._id} }")
    @NonNull private List<SubmissionEntity> submissions = new ArrayList<>();

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private Instant updatedAt;

    @Version
    @Field(name = "version")
    private Long version;
}

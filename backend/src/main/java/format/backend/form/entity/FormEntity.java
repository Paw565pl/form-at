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
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
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
@CompoundIndex(def = "{'status': 1, 'updatedAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'createdAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'submissionsCount': -1, '_id': 1}")
@Document(collection = "forms")
public class FormEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "name")
    @NonNull private String name;

    @Indexed(unique = true)
    @Field(name = "slug")
    @NonNull private String slug;

    @Field(name = "description")
    @Nullable private String description;

    @org.springframework.data.mongodb.core.mapping.Language
    @Field(name = "language")
    @NonNull private String language;

    @Field(name = "status")
    @NonNull private FormStatus status;

    @Field(name = "passwordHash")
    @Nullable private String passwordHash;

    @Field(name = "shuffleVariant")
    @Nullable private FormShuffleVariant shuffleVariant;

    @Field(name = "thanksMessage")
    @Nullable private String thanksMessage;

    @Field(name = "estimatedDurationSeconds")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @NonNull private Integer estimatedDurationSeconds;

    @Field(name = "thumbnailKey")
    @Nullable private String thumbnailKey;

    @Field(name = "allowsQuestionsPreview")
    @NonNull private Boolean allowsQuestionsPreview;

    @Field(name = "allowsGuestSubmissions")
    @NonNull private Boolean allowsGuestSubmissions;

    @Field(name = "saveSubmissions")
    @NonNull private Boolean saveSubmissions;

    @Field(name = "authorId")
    @DocumentReference(lazy = true)
    @Nullable private UserEntity author;

    @Field(name = "submissionsCount")
    @Setter(AccessLevel.NONE)
    @NonNull private Long submissionsCount = 0L;

    @Field(name = "questions")
    @Setter(AccessLevel.NONE)
    @NonNull private List<QuestionEntity> questions = new ArrayList<>();

    @ReadOnlyProperty
    @DocumentReference(lazy = true, lookup = "{'formId':?#{#self._id} }")
    @Setter(AccessLevel.NONE)
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

    @Transient
    @NonNull public Language getLanguage() {
        return Language.fromMongoValue(language)
                .orElseThrow(() -> new IllegalArgumentException("Invalid language mongo value: " + language));
    }

    public void setLanguage(@NonNull Language language) {
        this.language = language.getMongoValue();
    }

    @Transient
    @NonNull public Duration getEstimatedDuration() {
        return Duration.ofSeconds(estimatedDurationSeconds);
    }

    public void setEstimatedDuration(@NonNull Duration estimatedDuration) {
        this.estimatedDurationSeconds = (int) estimatedDuration.toSeconds();
    }
}

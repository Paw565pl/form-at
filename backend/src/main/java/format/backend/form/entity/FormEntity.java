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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(def = "{'status': 1, 'updatedAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'createdAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'questionsCount': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'submissionsCount': -1, '_id': 1}")
@CompoundIndex(def = "{'authorId': 1, 'updatedAt': -1, '_id': 1}")
@Document(collection = "forms")
public class FormEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "name")
    private @NonNull String name;

    @Indexed(unique = true)
    @Field(name = "slug")
    private @NonNull String slug;

    @Field(name = "description")
    private @Nullable String description;

    @org.springframework.data.mongodb.core.mapping.Language
    @Field(name = "language")
    private @NonNull String language;

    @Field(name = "status")
    private @NonNull FormStatus status;

    @Field(name = "passwordHash")
    private @Nullable String passwordHash;

    @Field(name = "shuffleVariant")
    private @Nullable FormShuffleVariant shuffleVariant;

    @Field(name = "thanksMessage")
    private @Nullable String thanksMessage;

    @Field(name = "estimatedDurationSeconds")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private @NonNull Integer estimatedDurationSeconds;

    @Field(name = "thumbnailKey")
    private @Nullable String thumbnailKey;

    @Field(name = "allowsQuestionsPreview")
    private @NonNull Boolean allowsQuestionsPreview;

    @Field(name = "allowsGuestSubmissions")
    private @NonNull Boolean allowsGuestSubmissions;

    @Field(name = "saveSubmissions")
    private @NonNull Boolean saveSubmissions;

    @Field(name = "questionsCount")
    private @NonNull Integer questionsCount;

    @Field(name = "submissionsCount")
    @Setter(AccessLevel.NONE)
    private @NonNull Long submissionsCount = 0L;

    @Field(name = "questions")
    @Setter(AccessLevel.NONE)
    private @NonNull List<@NonNull QuestionEntity> questions = new ArrayList<>();

    @ReadOnlyProperty
    @DocumentReference(lazy = true, lookup = "{'formId':?#{#self._id} }")
    @Setter(AccessLevel.NONE)
    private @NonNull List<@NonNull SubmissionEntity> submissions = new ArrayList<>();

    @Field("ratingsCount")
    private @NonNull Long ratingsCount = 0L;

    @Field("ratingsSum")
    private @NonNull Long ratingsSum = 0L;

    @Indexed
    @DocumentReference(lazy = true)
    @Field(name = "authorId")
    private @Nullable UserEntity author;

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
    public @NonNull Language getLanguage() {
        return Language.fromMongoValue(language)
                .orElseThrow(() -> new IllegalArgumentException("Invalid language mongo value: " + language));
    }

    public void setLanguage(@NonNull Language language) {
        this.language = language.getMongoValue();
    }

    @Transient
    public @NonNull Duration getEstimatedDuration() {
        return Duration.ofSeconds(estimatedDurationSeconds);
    }

    @Transient
    public @Nullable Double getRatingAvg() {
        return ratingsCount == 0 ? null : (double) ratingsSum / ratingsCount;
    }

    public void setEstimatedDuration(@NonNull Duration estimatedDuration) {
        this.estimatedDurationSeconds = (int) estimatedDuration.toSeconds();
    }
}

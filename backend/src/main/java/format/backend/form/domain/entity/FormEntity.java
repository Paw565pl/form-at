package format.backend.form.domain.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@CompoundIndex(def = "{'status': 1, 'createdAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'updatedAt': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'submissionsCount': -1, '_id': 1}")
@CompoundIndex(def = "{'status': 1, 'language': 1, 'createdAt': -1, '_id': 1}")
@CompoundIndex(def = "{'authorId': 1, 'createdAt': -1, '_id': 1}", partialFilter = "{'authorId': {'$type': 'string'}}")
@Document(collection = "forms")
public final class FormEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Field(name = "name")
    private String name;

    @Indexed(unique = true)
    @Field(name = "slug")
    private String slug;

    @Field(name = "description")
    private @Nullable String description;

    @Field(name = "language")
    private FormLanguage language;

    @Field(name = "status")
    private FormStatus status;

    @Field(name = "passwordHash")
    private @Nullable String passwordHash;

    @Field(name = "shuffleVariant")
    private @Nullable FormShuffleVariant shuffleVariant;

    @Field(name = "thanksMessage")
    private @Nullable String thanksMessage;

    @Field(name = "estimatedDurationSeconds")
    @Setter(AccessLevel.NONE)
    private long estimatedDurationSeconds;

    @Field(name = "thumbnailKey")
    private @Nullable String thumbnailKey;

    @Field(name = "allowsQuestionsPreview")
    private Boolean allowsQuestionsPreview;

    @Field(name = "allowsGuestSubmissions")
    private Boolean allowsGuestSubmissions;

    @Field(name = "saveSubmissions")
    private Boolean saveSubmissions;

    @Field(name = "showAnswersFeedback")
    private Boolean showAnswersFeedback;

    @Field(name = "questions")
    private final List<QuestionEntity> questions;

    @Field(name = "questionsCount")
    @Setter(AccessLevel.NONE)
    private int questionsCount;

    @Field(name = "submissionsCount")
    private final long submissionsCount;

    @Field("ratingsCount")
    private final long ratingsCount;

    @Field("ratingsSum")
    private final long ratingsSum;

    @Indexed(partialFilter = "{'authorId': {'$type': 'string'}}")
    @Field(name = "authorId")
    private final @Nullable String authorId;

    @CreatedDate
    @Field(name = "createdAt")
    private final @Nullable Instant createdAt;

    @LastModifiedDate
    @Field(name = "updatedAt")
    private final @Nullable Instant updatedAt;

    @Version
    @Field(name = "version")
    private final long version;

    @Builder
    public FormEntity(
            String name,
            String slug,
            @Nullable String description,
            FormLanguage language,
            FormStatus status,
            @Nullable String passwordHash,
            @Nullable FormShuffleVariant shuffleVariant,
            @Nullable String thanksMessage,
            Duration estimatedDuration,
            @Nullable String thumbnailKey,
            Boolean allowsQuestionsPreview,
            Boolean allowsGuestSubmissions,
            Boolean saveSubmissions,
            Boolean showAnswersFeedback,
            @Singular Collection<QuestionEntity> questions,
            String authorId) {
        this.id = null;
        this.name = Objects.requireNonNull(name);
        this.slug = Objects.requireNonNull(slug);
        this.description = description;
        this.language = Objects.requireNonNull(language);
        this.status = Objects.requireNonNull(status);
        this.passwordHash = passwordHash;
        this.shuffleVariant = shuffleVariant;
        this.thanksMessage = thanksMessage;
        this.estimatedDurationSeconds =
                Objects.requireNonNull(estimatedDuration).toSeconds();
        this.thumbnailKey = thumbnailKey;
        this.allowsQuestionsPreview = Objects.requireNonNull(allowsQuestionsPreview);
        this.allowsGuestSubmissions = Objects.requireNonNull(allowsGuestSubmissions);
        this.saveSubmissions = Objects.requireNonNull(saveSubmissions);
        this.showAnswersFeedback = Objects.requireNonNull(showAnswersFeedback);
        this.questions = new ArrayList<>(Objects.requireNonNull(questions));
        this.questionsCount = this.questions.size();
        this.submissionsCount = 0;
        this.ratingsCount = 0;
        this.ratingsSum = 0;
        this.authorId = Objects.requireNonNull(authorId);
        this.createdAt = null;
        this.updatedAt = null;
        this.version = 0;
    }

    public Duration getEstimatedDuration() {
        return Duration.ofSeconds(estimatedDurationSeconds);
    }

    public void setEstimatedDuration(Duration duration) {
        this.estimatedDurationSeconds = duration.toSeconds();
    }

    public List<QuestionEntity> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    /// returns invalidated question ids
    public Set<String> updateQuestions(List<QuestionEntity> questions) {
        val unmatchedQuestions = new ArrayList<>(this.questions);
        val newQuestions = new ArrayList<QuestionEntity>(questions.size());

        for (val question : questions) {
            unmatchedQuestions.stream()
                    .filter(question::hasSameContentAs)
                    .findFirst()
                    .ifPresentOrElse(
                            matchedQuestion -> {
                                unmatchedQuestions.remove(matchedQuestion);
                                newQuestions.add(matchedQuestion);
                            },
                            () -> newQuestions.add(question));
        }

        val invalidatedQuestionIds =
                unmatchedQuestions.stream().map(QuestionEntity::getId).collect(Collectors.toUnmodifiableSet());

        this.questions.clear();
        this.questions.addAll(newQuestions);
        this.questionsCount = this.questions.size();

        return invalidatedQuestionIds;
    }

    public @Nullable Double getRatingAvg() {
        return ratingsCount == 0 ? null : (double) ratingsSum / ratingsCount;
    }
}

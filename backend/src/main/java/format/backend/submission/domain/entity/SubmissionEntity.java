package format.backend.submission.domain.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@CompoundIndex(def = "{'formId': 1, 'authorId': 1}", partialFilter = "{'authorId': {'$type': 'string'}}", unique = true)
@CompoundIndex(def = "{'formId': 1, '_id': -1}")
@Document(collection = "submissions")
public final class SubmissionEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final @Nullable String id;

    @Indexed
    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private final String formId;

    @Indexed(partialFilter = "{'authorId': {'$type': 'string'}}")
    @Field(name = "authorId")
    private final @Nullable String authorId;

    @Field(name = "answers")
    private final List<SubmissionAnswerEntity> answers;

    @CreatedDate
    @Field(name = "createdAt")
    private final @Nullable Instant createdAt;

    @Builder
    public SubmissionEntity(
            String formId, @Nullable String authorId, @Singular Collection<SubmissionAnswerEntity> answers) {
        this.id = null;
        this.formId = Objects.requireNonNull(formId);
        this.authorId = authorId;
        this.answers = List.copyOf(Objects.requireNonNull(answers));
        this.createdAt = null;
    }
}

package format.backend.submission.domain.entity;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
public final class SubmissionAnswerEntity {

    @Field(name = "questionId", targetType = FieldType.OBJECT_ID)
    private final String questionId;

    @Field(name = "chosenAnswerIds", targetType = FieldType.OBJECT_ID)
    private final Set<String> chosenAnswerIds;

    @Field(name = "openAnswer")
    private final @Nullable String openAnswer;

    public static SubmissionAnswerEntity forQuestionWithAnswers(String questionId, Collection<String> chosenAnswerIds) {
        return new SubmissionAnswerEntity(
                Objects.requireNonNull(questionId), Set.copyOf(Objects.requireNonNull(chosenAnswerIds)), null);
    }

    public static SubmissionAnswerEntity forOpenQuestion(String questionId, String openAnswer) {
        return new SubmissionAnswerEntity(
                Objects.requireNonNull(questionId),
                Set.of(),
                Objects.requireNonNull(openAnswer).trim());
    }
}

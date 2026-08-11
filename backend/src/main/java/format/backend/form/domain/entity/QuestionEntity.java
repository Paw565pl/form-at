package format.backend.form.domain.entity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
public final class QuestionEntity {

    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final String id;

    @Field(name = "content")
    private final String content;

    @Field(name = "type")
    private final QuestionType type;

    @Field(name = "imageKey")
    private final @Nullable String imageKey;

    @Field(name = "isRequired")
    private final Boolean isRequired;

    @Field(name = "answers")
    private final List<AnswerEntity> answers;

    @Builder
    public QuestionEntity(
            String content,
            QuestionType type,
            @Nullable String imageKey,
            Boolean isRequired,
            @Singular Collection<AnswerEntity> answers) {
        this.id = ObjectId.get().toHexString();
        this.content = Objects.requireNonNull(content);
        this.type = Objects.requireNonNull(type);
        this.imageKey = imageKey;
        this.isRequired = Objects.requireNonNull(isRequired);
        this.answers = this.type == QuestionType.OPEN ? List.of() : List.copyOf(Objects.requireNonNull(answers));
    }
}

package format.backend.form.domain.entity;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
public final class AnswerEntity {

    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private final String id;

    @Field(name = "content")
    private final String content;

    @Field(name = "isCorrect")
    private final Boolean isCorrect;

    @Builder
    public AnswerEntity(String content, Boolean isCorrect) {
        this.id = ObjectId.get().toHexString();
        this.content = Objects.requireNonNull(content);
        this.isCorrect = Objects.requireNonNull(isCorrect);
    }

    /// compare content ignoring id
    public boolean hasSameContentAs(AnswerEntity other) {
        return Objects.equals(this.content, other.content) && Objects.equals(this.isCorrect, other.isCorrect);
    }
}

package format.backend.form.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerEntity {

    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id = ObjectId.get().toHexString();

    @Field(name = "content")
    private @NonNull String content;

    @Field(name = "isCorrect")
    private @NonNull Boolean isCorrect;
}

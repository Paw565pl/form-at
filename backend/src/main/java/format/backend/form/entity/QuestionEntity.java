package format.backend.form.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionEntity {

    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id = ObjectId.get().toHexString();

    @Field(name = "content")
    private @NonNull String content;

    @Field(name = "type")
    private @NonNull QuestionType type;

    @Field(name = "imageKey")
    private @Nullable String imageKey;

    @Field(name = "isRequired")
    private @NonNull Boolean isRequired;

    @Field(name = "answers")
    @Setter(AccessLevel.NONE)
    private @NonNull List<@NonNull AnswerEntity> answers = new ArrayList<>();
}

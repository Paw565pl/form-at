package format.backend.form.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionEntity {

    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id = ObjectId.get().toHexString();

    @Field(name = "content")
    @NonNull private String content;

    @Field(name = "type")
    @NonNull private QuestionType type;

    @Field(name = "imageKey")
    @Nullable private String imageKey;

    @Field(name = "isRequired")
    @NonNull private Boolean isRequired;

    @Field(name = "answers")
    @NonNull private List<AnswerEntity> answers = new ArrayList<>();
}

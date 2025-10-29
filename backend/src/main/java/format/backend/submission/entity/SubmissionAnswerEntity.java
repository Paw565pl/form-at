package format.backend.submission.entity;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionAnswerEntity {

    @Field(name = "questionId", targetType = FieldType.OBJECT_ID)
    @NonNull private String questionId;

    @Field(name = "chosenAnswerIds", targetType = FieldType.OBJECT_ID)
    @NonNull private Set<String> chosenAnswerIds = new HashSet<>();

    @Field(name = "openAnswer")
    @Nullable private String openAnswer;
}

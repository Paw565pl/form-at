package format.backend.submission.entity;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionAnswerEntity {

    @Field(name = "questionId", targetType = FieldType.OBJECT_ID)
    private @NonNull String questionId;

    @Field(name = "chosenAnswerIds", targetType = FieldType.OBJECT_ID)
    @Setter(AccessLevel.NONE)
    private @NonNull Set<@NonNull String> chosenAnswerIds = new HashSet<>();

    @Field(name = "openAnswer")
    private @Nullable String openAnswer;
}

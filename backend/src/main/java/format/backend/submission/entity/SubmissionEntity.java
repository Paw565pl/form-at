package format.backend.submission.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(def = "{'formId': 1, 'authorId': 1}", unique = true, partialFilter = "{'authorId': {'$type': 'string'}}")
@Document(collection = "submissions")
public class SubmissionEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed
    @Field(name = "formId")
    private @NonNull String formId;

    @Indexed
    @Field(name = "authorId")
    private @Nullable String authorId;

    @Field(name = "answers")
    @Setter(AccessLevel.NONE)
    private @NonNull List<@NonNull SubmissionAnswerEntity> answers = new ArrayList<>();

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;
}

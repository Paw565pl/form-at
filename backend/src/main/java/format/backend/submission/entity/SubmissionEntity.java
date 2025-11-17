package format.backend.submission.entity;

import format.backend.auth.entity.UserEntity;
import format.backend.form.entity.FormEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CompoundIndex(def = "{'formId': 1, 'userId': 1}", unique = true, partialFilter = "{'userId': {'$exists': true}}")
@Document(collection = "submissions")
public class SubmissionEntity {

    @MongoId
    @Field(name = "_id", targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "formId")
    @DocumentReference(lazy = true)
    @NonNull private FormEntity form;

    @Field(name = "userId")
    @DocumentReference(lazy = true)
    @Nullable private UserEntity author;

    @Field(name = "answers")
    @Setter(AccessLevel.NONE)
    @NonNull private List<SubmissionAnswerEntity> answers = new ArrayList<>();

    @CreatedDate
    @Field(name = "createdAt")
    private Instant createdAt;
}

package format.backend.submission.entity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "submissionsStatistics")
public class SubmissionsStatisticsEntity {

    @MongoId
    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private @NonNull String formId;

    /** key is questionId */
    @Field(name = "questions")
    @Setter(AccessLevel.NONE)
    private @NonNull Map<String, Statistics> questions = new HashMap<>();

    @LastModifiedDate
    @Field(name = "updatedAt")
    private Instant updatedAt;

    @Version
    @Field(name = "version")
    private Long version;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Statistics {

        /**
         * key is questionId <br>
         * value is submitted answers count
         */
        @Field(name = "answers")
        @Setter(AccessLevel.NONE)
        private @NonNull Map<String, Long> answers = new HashMap<>();
    }

    public static String getPath(String questionId, String answerId) {
        return "questions." + questionId + ".answers." + answerId;
    }
}

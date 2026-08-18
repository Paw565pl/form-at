package format.backend.submission.domain.entity;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Document(collection = "submissionsStatistics")
public final class SubmissionsStatisticsEntity {

    @MongoId
    @Field(name = "formId", targetType = FieldType.OBJECT_ID)
    private final String formId;

    /// key: questionId
    @Field(name = "questions")
    private final Map<String, Statistics> questions;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
    public static final class Statistics {

        /// key: answerId <br>
        /// value: submitted answers count
        @Field(name = "answers")
        private final Map<String, Long> answers;
    }

    public static String getQuestionPath(String questionId) {
        return "questions.%s".formatted(questionId);
    }

    public static String getQuestionAnswerPath(String questionId, String answerId) {
        return "questions.%s.answers.%s".formatted(questionId, answerId);
    }
}

package format.backend.form.datafactory;

import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.entity.QuestionType;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public abstract class FormTestDataFactory {

    public static FormEntity create() {
        return create(FormStatus.PUBLIC);
    }

    public static FormEntity create(FormStatus status) {
        return create(status, null);
    }

    public static FormEntity create(FormStatus status, String authorId) {
        var questionA = new QuestionEntity("question A", QuestionType.SINGLE_CHOICE, true);
        questionA.getAnswers().addAll(List.of(new AnswerEntity("answer A", true), new AnswerEntity("answer B", false)));

        var questionB = new QuestionEntity("question B", QuestionType.MULTIPLE_CHOICE, true);
        questionB.getAnswers().addAll(List.of(new AnswerEntity("answer A", true), new AnswerEntity("answer B", false)));

        var questionC = new QuestionEntity("question C", QuestionType.OPEN, true);

        var formEntity = new FormEntity(
                "test form",
                "slug-" + UUID.randomUUID(),
                Language.EN.getValue(),
                status,
                (int) Duration.ofMinutes(1).toSeconds(),
                true,
                true,
                true,
                true,
                3);
        formEntity.setAuthorId(authorId);
        formEntity.getQuestions().addAll(List.of(questionA, questionB, questionC));

        return formEntity;
    }
}

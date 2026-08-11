package format.backend.form.datafactory;

import format.backend.form.application.shared.dto.AnswerRequestDto;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.domain.entity.FormLanguage;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.QuestionType;
import java.time.Duration;
import java.util.List;
import lombok.val;

public abstract class FormRequestDtoTestDataFactory {

    public static FormRequestDto createValidPublic() {
        return createValid(FormStatus.PUBLIC, null);
    }

    public static FormRequestDto createValidPrivate(String password) {
        return createValid(FormStatus.PRIVATE, password);
    }

    public static FormRequestDto createValid(FormStatus status, String password) {
        val validQuestion = new QuestionRequestDto(
                "question",
                QuestionType.SINGLE_CHOICE,
                null,
                true,
                List.of(new AnswerRequestDto("answer a", true), new AnswerRequestDto("answer b", false)));

        return create(status, password, List.of(validQuestion, validQuestion, validQuestion));
    }

    public static FormRequestDto createWithInvalidQuestionAnswers() {
        val invalidQuestion = new QuestionRequestDto(
                "question",
                QuestionType.SINGLE_CHOICE,
                null,
                true,
                List.of(new AnswerRequestDto("answer a", false), new AnswerRequestDto("answer b", false)));

        return create(FormStatus.PUBLIC, null, List.of(invalidQuestion, invalidQuestion, invalidQuestion));
    }

    public static FormRequestDto create(FormStatus status, String password, List<QuestionRequestDto> questions) {
        return new FormRequestDto(
                "form",
                null,
                FormLanguage.EN,
                status,
                password,
                null,
                null,
                Duration.ofMinutes(15),
                null,
                true,
                true,
                true,
                true,
                questions);
    }
}

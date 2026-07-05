package format.backend.form.datafactory;

import format.backend.form.dto.AnswerRequestDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import format.backend.form.entity.QuestionType;
import java.time.Duration;
import java.util.List;

public abstract class FormRequestDtoTestDataFactory {

    public static FormRequestDto createValidPublic() {
        return createValid(FormStatus.PUBLIC, null);
    }

    public static FormRequestDto createValidPrivate(String password) {
        return createValid(FormStatus.PRIVATE, password);
    }

    public static FormRequestDto createValid(FormStatus status, String password) {
        var validQuestion = new QuestionRequestDto(
                "question",
                QuestionType.SINGLE_CHOICE,
                null,
                true,
                List.of(new AnswerRequestDto("answer a", true), new AnswerRequestDto("answer b", false)));

        return create(status, password, List.of(validQuestion, validQuestion, validQuestion));
    }

    public static FormRequestDto createWithInvalidQuestionAnswers() {
        var invalidQuestion = new QuestionRequestDto(
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
                Language.EN,
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

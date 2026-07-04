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
        return createBaseFormRequest(FormStatus.PUBLIC, null);
    }

    public static FormRequestDto createPublicWithCustomQuestions(List<QuestionRequestDto> questions) {
        return new FormRequestDto(
                "form",
                null,
                Language.EN,
                FormStatus.PUBLIC,
                null,
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

    public static FormRequestDto createValidPrivate(String password) {
        return createBaseFormRequest(FormStatus.PRIVATE, password);
    }

    public static FormRequestDto createWithInvalidQuestionAnswers() {
        var invalidQuestion = new QuestionRequestDto(
                "question",
                QuestionType.SINGLE_CHOICE,
                null,
                true,
                List.of(new AnswerRequestDto("answer a", false), new AnswerRequestDto("answer b", false)));

        return new FormRequestDto(
                "form dto",
                null,
                Language.EN,
                FormStatus.PUBLIC,
                null,
                null,
                null,
                Duration.ofMinutes(10),
                null,
                true,
                true,
                true,
                true,
                List.of(invalidQuestion, invalidQuestion, invalidQuestion));
    }

    private static FormRequestDto createBaseFormRequest(FormStatus status, String password) {
        var validQuestion = new QuestionRequestDto(
                "question",
                QuestionType.SINGLE_CHOICE,
                null,
                true,
                List.of(new AnswerRequestDto("answer a", true), new AnswerRequestDto("answer b", false)));

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
                List.of(validQuestion, validQuestion, validQuestion));
    }
}

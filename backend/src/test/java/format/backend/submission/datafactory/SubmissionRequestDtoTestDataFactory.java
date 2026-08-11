package format.backend.submission.datafactory;

import format.backend.form.domain.entity.AnswerEntity;
import format.backend.form.domain.entity.FormEntity;
import format.backend.submission.application.shared.dto.SubmissionAnswerRequestDto;
import format.backend.submission.application.shared.dto.SubmissionRequestDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

public abstract class SubmissionRequestDtoTestDataFactory {

    public static SubmissionRequestDto createValid(FormEntity form) {
        val answers = form.getQuestions().stream()
                .map(question -> switch (question.getType()) {
                    case OPEN -> new SubmissionAnswerRequestDto(question.getId(), Set.of(), "open answer text");
                    case SINGLE_CHOICE ->
                        new SubmissionAnswerRequestDto(
                                question.getId(),
                                Set.of(question.getAnswers().getFirst().getId()),
                                null);
                    case MULTIPLE_CHOICE ->
                        new SubmissionAnswerRequestDto(
                                question.getId(),
                                question.getAnswers().stream()
                                        .map(AnswerEntity::getId)
                                        .collect(Collectors.toUnmodifiableSet()),
                                null);
                })
                .toList();

        return new SubmissionRequestDto(answers);
    }

    public static SubmissionRequestDto createValidWithOverriddenAnswer(
            FormEntity form, SubmissionAnswerRequestDto overrideAnswer) {
        val validRequest = createValid(form);
        val modifiedAnswers = validRequest.answers().stream()
                .map(a -> a.questionId().equals(overrideAnswer.questionId()) ? overrideAnswer : a)
                .toList();

        return new SubmissionRequestDto(modifiedAnswers);
    }

    public static SubmissionRequestDto create(List<SubmissionAnswerRequestDto> answers) {
        return new SubmissionRequestDto(answers);
    }
}

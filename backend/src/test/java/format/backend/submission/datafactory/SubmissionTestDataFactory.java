package format.backend.submission.datafactory;

import format.backend.form.domain.entity.AnswerEntity;
import format.backend.form.domain.entity.FormEntity;
import format.backend.submission.domain.entity.SubmissionAnswerEntity;
import format.backend.submission.domain.entity.SubmissionEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

public abstract class SubmissionTestDataFactory {

    public static SubmissionEntity create(String formId, String authorId, List<SubmissionAnswerEntity> answers) {
        return SubmissionEntity.builder()
                .formId(formId)
                .authorId(authorId)
                .answers(answers)
                .build();
    }

    public static SubmissionEntity createValid(FormEntity form, String authorId) {
        val answers = form.getQuestions().stream()
                .map(question -> switch (question.getType()) {
                    case OPEN -> SubmissionAnswerEntity.forOpenQuestion(question.getId(), "open answer text");
                    case SINGLE_CHOICE ->
                        SubmissionAnswerEntity.forQuestionWithAnswers(
                                question.getId(),
                                Set.of(question.getAnswers().getFirst().getId()));

                    case MULTIPLE_CHOICE ->
                        SubmissionAnswerEntity.forQuestionWithAnswers(
                                question.getId(),
                                question.getAnswers().stream()
                                        .map(AnswerEntity::getId)
                                        .collect(Collectors.toUnmodifiableSet()));
                })
                .toList();
        return SubmissionEntity.builder()
                .formId(form.getId())
                .authorId(authorId)
                .answers(answers)
                .build();
    }
}

package format.backend.submission.datafactory;

import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.FormEntity;
import format.backend.submission.entity.SubmissionAnswerEntity;
import format.backend.submission.entity.SubmissionEntity;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SubmissionTestDataFactory {

    public static SubmissionEntity create(String formId, String authorId, List<SubmissionAnswerEntity> answers) {
        var submissionEntity = new SubmissionEntity(formId);
        submissionEntity.setAuthorId(authorId);
        submissionEntity.getAnswers().addAll(answers);

        return submissionEntity;
    }

    public static SubmissionEntity createValid(FormEntity form, String authorId) {
        var answers = form.getQuestions().stream()
                .map(question -> {
                    var submissionAnswerEntity = new SubmissionAnswerEntity(question.getId());
                    switch (question.getType()) {
                        case OPEN -> submissionAnswerEntity.setOpenAnswer("open answer text");
                        case SINGLE_CHOICE ->
                            submissionAnswerEntity
                                    .getChosenAnswerIds()
                                    .add(question.getAnswers().getFirst().getId());
                        case MULTIPLE_CHOICE ->
                            submissionAnswerEntity
                                    .getChosenAnswerIds()
                                    .addAll(question.getAnswers().stream()
                                            .map(AnswerEntity::getId)
                                            .collect(Collectors.toUnmodifiableSet()));
                    }

                    return submissionAnswerEntity;
                })
                .toList();

        var submission = new SubmissionEntity(form.getId());
        submission.setAuthorId(authorId);
        submission.getAnswers().addAll(answers);

        return submission;
    }
}

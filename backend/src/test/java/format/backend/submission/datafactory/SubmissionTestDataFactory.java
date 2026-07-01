package format.backend.submission.datafactory;

import format.backend.submission.entity.SubmissionAnswerEntity;
import format.backend.submission.entity.SubmissionEntity;
import java.util.List;

public abstract class SubmissionTestDataFactory {

    public static SubmissionEntity create(String formId, String authorId, List<SubmissionAnswerEntity> answers) {
        var submissionEntity = new SubmissionEntity(formId);
        submissionEntity.setAuthorId(authorId);
        submissionEntity.getAnswers().addAll(answers);

        return submissionEntity;
    }
}

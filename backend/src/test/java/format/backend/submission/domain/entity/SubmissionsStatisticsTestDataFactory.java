package format.backend.submission.domain.entity;

import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.QuestionType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

public abstract class SubmissionsStatisticsTestDataFactory {

    public static SubmissionsStatisticsEntity create(
            String formId, Map<String, SubmissionsStatisticsEntity.Statistics> questions) {
        return new SubmissionsStatisticsEntity(formId, questions);
    }

    public static SubmissionsStatisticsEntity createInitializedWithSubmission(
            FormEntity form, SubmissionEntity submission) {
        val chosenAnswersByQuestion = submission.getAnswers().stream()
                .collect(Collectors.toMap(
                        SubmissionAnswerEntity::getQuestionId, SubmissionAnswerEntity::getChosenAnswerIds));

        val questionsMap = new HashMap<String, SubmissionsStatisticsEntity.Statistics>();
        for (val question : form.getQuestions()) {
            if (question.getType() == QuestionType.OPEN) continue;

            val chosenForQuestion = chosenAnswersByQuestion.getOrDefault(question.getId(), Set.of());

            val answersMap = new HashMap<String, Long>();
            for (val answer : question.getAnswers()) {
                val count = chosenForQuestion.contains(answer.getId()) ? 1L : 0L;
                answersMap.put(answer.getId(), count);
            }

            questionsMap.put(question.getId(), new SubmissionsStatisticsEntity.Statistics(answersMap));
        }

        return create(form.getId(), questionsMap);
    }
}

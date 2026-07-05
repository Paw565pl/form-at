package format.backend.submission.datafactory;

import format.backend.form.entity.FormEntity;
import format.backend.form.entity.QuestionType;
import format.backend.submission.entity.SubmissionAnswerEntity;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SubmissionsStatisticsTestDataFactory {

    public static SubmissionsStatisticsEntity create(
            String formId, Map<String, SubmissionsStatisticsEntity.Statistics> questions) {
        var submissionsStatistics = new SubmissionsStatisticsEntity(formId);
        submissionsStatistics.getQuestions().putAll(questions);

        return submissionsStatistics;
    }

    public static SubmissionsStatisticsEntity createInitializedWithSubmission(
            FormEntity form, SubmissionEntity submission) {
        var chosenAnswersByQuestion = submission.getAnswers().stream()
                .collect(Collectors.toMap(
                        SubmissionAnswerEntity::getQuestionId, SubmissionAnswerEntity::getChosenAnswerIds));

        var questionsMap = new HashMap<String, SubmissionsStatisticsEntity.Statistics>();
        for (var question : form.getQuestions()) {
            if (question.getType() == QuestionType.OPEN) continue;

            var chosenForQuestion = chosenAnswersByQuestion.getOrDefault(question.getId(), Set.of());

            var answersMap = new HashMap<String, Long>();
            for (var answer : question.getAnswers()) {
                var count = chosenForQuestion.contains(answer.getId()) ? 1L : 0L;
                answersMap.put(answer.getId(), count);
            }

            questionsMap.put(question.getId(), new SubmissionsStatisticsEntity.Statistics(answersMap));
        }

        return create(form.getId(), questionsMap);
    }
}

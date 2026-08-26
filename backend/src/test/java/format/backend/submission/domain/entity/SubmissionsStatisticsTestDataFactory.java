package format.backend.submission.domain.entity;

import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.QuestionType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;

public final class SubmissionsStatisticsTestDataFactory {

    private SubmissionsStatisticsTestDataFactory() {}

    public static SubmissionsStatisticsEntity create(
            String formId, Map<String, Map<String, Long>> answersCountByQuestionId) {
        val statisticsByQuestionId = answersCountByQuestionId.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), new SubmissionsStatisticsEntity.Statistics(entry.getValue())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        return new SubmissionsStatisticsEntity(formId, statisticsByQuestionId);
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

        return new SubmissionsStatisticsEntity(form.getId(), questionsMap);
    }
}

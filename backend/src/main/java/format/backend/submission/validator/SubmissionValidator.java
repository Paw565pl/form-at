package format.backend.submission.validator;

import format.backend.form.entity.AnswerEntity;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.QuestionEntity;
import format.backend.submission.dto.SubmissionAnswerRequestDto;
import format.backend.submission.dto.SubmissionRequestDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionValidator {

    public Map<String, List<String>> validate(FormEntity form, SubmissionRequestDto submission) {
        val errors = new HashMap<String, List<String>>();

        validateRequiredQuestionsHaveAnswers(form, submission).ifPresent(e -> errors.put(e.getKey(), e.getValue()));
        validateAnswers(form, submission).forEach(e -> errors.put(e.getKey(), e.getValue()));

        return Collections.unmodifiableMap(errors);
    }

    private Optional<Map.Entry<String, List<String>>> validateRequiredQuestionsHaveAnswers(
            FormEntity form, SubmissionRequestDto submission) {
        val submissionAnswersQuestionsIds = submission.answers().stream()
                .map(SubmissionAnswerRequestDto::questionId)
                .collect(Collectors.toUnmodifiableSet());
        val notAnsweredRequiredQuestionIds = form.getQuestions().stream()
                .filter(QuestionEntity::getIsRequired)
                .map(QuestionEntity::getId)
                .filter(id -> !submissionAnswersQuestionsIds.contains(id))
                .toList();
        if (notAnsweredRequiredQuestionIds.isEmpty()) return Optional.empty();

        val message = String.format(
                "Required questions with ids '%s' were not answered",
                String.join(", ", notAnsweredRequiredQuestionIds));
        val entry = Map.entry("answers", List.of(message));

        return Optional.of(entry);
    }

    private List<Map.Entry<String, List<String>>> validateAnswers(FormEntity form, SubmissionRequestDto submission) {
        val questionsById = form.getQuestions().stream()
                .collect(Collectors.toUnmodifiableMap(QuestionEntity::getId, Function.identity()));
        val questionIdToAnswersIdsMap = form.getQuestions().stream()
                .collect(Collectors.toUnmodifiableMap(
                        QuestionEntity::getId,
                        q -> q.getAnswers().stream().map(AnswerEntity::getId).collect(Collectors.toUnmodifiableSet())));

        val submissionAnswers = submission.answers();

        val errors = new ArrayList<Map.Entry<String, List<String>>>();
        for (var i = 0; i < submissionAnswers.size(); i++) {
            val submissionAnswer = submissionAnswers.get(i);
            val question = questionsById.get(submissionAnswer.questionId());
            val answersIds = questionIdToAnswersIdsMap.get(submissionAnswer.questionId());

            if (question == null || answersIds == null) {
                val message = String.format(
                        "Form with id '%s' does not have question with id '%s'",
                        form.getId(), submissionAnswer.questionId());
                errors.add(Map.entry(String.format("answers[%s].questionId", i), List.of(message)));
                continue;
            }

            switch (question.getType()) {
                case SINGLE_CHOICE -> {
                    val errorMessages = validateSingleChoiceQuestionAnswer(submissionAnswer, answersIds);
                    if (!errorMessages.isEmpty())
                        errors.add(Map.entry(String.format("answers[%s].chosenAnswerIds", i), errorMessages));
                }
                case MULTIPLE_CHOICE -> {
                    val errorMessages = validateMultipleChoiceQuestionAnswer(submissionAnswer, answersIds);
                    if (!errorMessages.isEmpty())
                        errors.add(Map.entry(String.format("answers[%s].chosenAnswerIds", i), errorMessages));
                }
                case OPEN -> {
                    val errorMessages = validateOpenQuestionAnswer(submissionAnswer);
                    if (!errorMessages.isEmpty())
                        errors.add(Map.entry(String.format("answers[%s].openAnswer", i), errorMessages));
                }
            }
        }

        return errors;
    }

    private List<String> validateSingleChoiceQuestionAnswer(
            SubmissionAnswerRequestDto submissionAnswer, Set<String> answersIds) {
        val errorMessages = new ArrayList<String>();

        if (submissionAnswer.chosenAnswerIds().size() != 1) {
            errorMessages.add("Single choice question must have exactly one answer");
        }

        if (!answersIds.containsAll(submissionAnswer.chosenAnswerIds())) {
            val invalidAnswerIds = submissionAnswer.chosenAnswerIds().stream()
                    .filter(a -> !answersIds.contains(a))
                    .toList();

            val message = String.format(
                    "Given answer ids '%s' are not valid. Valid ids are '%s'",
                    String.join(", ", invalidAnswerIds), String.join(", ", answersIds));
            errorMessages.add(message);
        }

        return Collections.unmodifiableList(errorMessages);
    }

    private List<String> validateMultipleChoiceQuestionAnswer(
            SubmissionAnswerRequestDto submissionAnswer, Set<String> answersIds) {
        val errorMessages = new ArrayList<String>();

        val hasInvalidAnswersCount = submissionAnswer.chosenAnswerIds().isEmpty()
                || submissionAnswer.chosenAnswerIds().size() > answersIds.size();
        if (hasInvalidAnswersCount) {
            val message =
                    String.format("Multiple choice question must have between 1 and %s answers", answersIds.size());
            errorMessages.add(message);
        }

        if (!answersIds.containsAll(submissionAnswer.chosenAnswerIds())) {
            val invalidAnswerIds = submissionAnswer.chosenAnswerIds().stream()
                    .filter(a -> !answersIds.contains(a))
                    .toList();
            val message = String.format(
                    "Given answer ids '%s' are not valid. Valid ids are '%s'",
                    String.join(", ", invalidAnswerIds), String.join(", ", answersIds));

            errorMessages.add(message);
        }

        return Collections.unmodifiableList(errorMessages);
    }

    private List<String> validateOpenQuestionAnswer(SubmissionAnswerRequestDto submissionAnswer) {
        val errorMessages = new ArrayList<String>();

        if (submissionAnswer.openAnswer() == null
                || submissionAnswer.openAnswer().isBlank())
            errorMessages.add("Open answer question must have non-blank open answer");

        return Collections.unmodifiableList(errorMessages);
    }
}

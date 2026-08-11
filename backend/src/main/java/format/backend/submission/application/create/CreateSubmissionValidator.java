package format.backend.submission.application.create;

import format.backend.form.FormView;
import format.backend.form.QuestionView;
import format.backend.submission.application.shared.dto.SubmissionAnswerRequestDto;
import format.backend.submission.application.shared.dto.SubmissionRequestDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
class CreateSubmissionValidator {

    public Map<String, List<String>> validate(FormView formView, SubmissionRequestDto requestDto) {
        val errors = new LinkedHashMap<String, List<String>>();

        validateRequiredQuestionsHaveAnswers(formView, requestDto, errors);
        validateAnswers(formView, requestDto, errors);

        return Collections.unmodifiableMap(errors);
    }

    private static void validateRequiredQuestionsHaveAnswers(
            FormView formView, SubmissionRequestDto requestDto, Map<String, List<String>> errors) {
        val requestQuestionIds = requestDto.answers().stream()
                .map(SubmissionAnswerRequestDto::questionId)
                .collect(Collectors.toUnmodifiableSet());
        val notAnsweredRequiredQuestionIds = formView.questions().stream()
                .filter(QuestionView::isRequired)
                .map(QuestionView::id)
                .filter(id -> !requestQuestionIds.contains(id))
                .collect(Collectors.joining(", "));

        if (notAnsweredRequiredQuestionIds.isEmpty()) return;

        val message = "Required questions with ids '%s' were not answered".formatted(notAnsweredRequiredQuestionIds);
        errors.put("answers", List.of(message));
    }

    private static void validateAnswers(
            FormView formView, SubmissionRequestDto requestDto, Map<String, List<String>> errors) {
        val questionViewsById = formView.questions().stream()
                .collect(Collectors.toUnmodifiableMap(QuestionView::id, Function.identity()));

        val submissionsAnswers = requestDto.answers();
        for (var i = 0; i < submissionsAnswers.size(); i++) {
            val submissionAnswer = submissionsAnswers.get(i);
            val questionView = questionViewsById.get(submissionAnswer.questionId());

            if (questionView == null) {
                val message = "Form with id '%s' does not have question with id '%s'"
                        .formatted(formView.id(), submissionAnswer.questionId());
                errors.put("answers[%d].questionId".formatted(i), List.of(message));

                continue;
            }

            switch (questionView.type()) {
                case SINGLE_CHOICE -> {
                    val errorMessages = validateSingleChoiceQuestionAnswer(questionView, submissionAnswer);
                    if (!errorMessages.isEmpty()) errors.put("answers[%d].chosenAnswerIds".formatted(i), errorMessages);
                }
                case MULTIPLE_CHOICE -> {
                    val errorMessages = validateMultipleChoiceQuestionAnswer(questionView, submissionAnswer);
                    if (!errorMessages.isEmpty()) errors.put("answers[%d].chosenAnswerIds".formatted(i), errorMessages);
                }
                case OPEN -> {
                    val errorMessages = validateOpenQuestionAnswer(submissionAnswer);
                    if (!errorMessages.isEmpty()) errors.put("answers[%d].openAnswer".formatted(i), errorMessages);
                }
            }
        }
    }

    private static List<String> validateSingleChoiceQuestionAnswer(
            QuestionView questionView, SubmissionAnswerRequestDto submissionAnswerRequestDto) {
        val errorMessages = new ArrayList<String>();

        if (submissionAnswerRequestDto.chosenAnswerIds().size() != 1) {
            errorMessages.add("Single choice question must have exactly one answer");
        }

        val answersIds = questionView.answerIds();
        if (!answersIds.containsAll(submissionAnswerRequestDto.chosenAnswerIds())) {
            val invalidAnswerIds = submissionAnswerRequestDto.chosenAnswerIds().stream()
                    .filter(a -> !answersIds.contains(a))
                    .collect(Collectors.joining(", "));

            val message = "Given answer ids '%s' are not valid. Valid ids are '%s'"
                    .formatted(invalidAnswerIds, String.join(", ", answersIds));
            errorMessages.add(message);
        }

        return Collections.unmodifiableList(errorMessages);
    }

    private static List<String> validateMultipleChoiceQuestionAnswer(
            QuestionView questionView, SubmissionAnswerRequestDto submissionAnswerRequestDto) {
        val errorMessages = new ArrayList<String>();
        val answersIds = questionView.answerIds();

        val hasInvalidAnswersCount =
                submissionAnswerRequestDto.chosenAnswerIds().isEmpty()
                        || submissionAnswerRequestDto.chosenAnswerIds().size() > answersIds.size();
        if (hasInvalidAnswersCount) {
            errorMessages.add("Question must have between 1 and %d answers".formatted(answersIds.size()));
        }

        if (!answersIds.containsAll(submissionAnswerRequestDto.chosenAnswerIds())) {
            val invalidAnswerIds = submissionAnswerRequestDto.chosenAnswerIds().stream()
                    .filter(a -> !answersIds.contains(a))
                    .collect(Collectors.joining(", "));

            val message = "Given answer ids '%s' are not valid. Valid ids are '%s'"
                    .formatted(invalidAnswerIds, String.join(", ", answersIds));
            errorMessages.add(message);
        }

        return Collections.unmodifiableList(errorMessages);
    }

    private static List<String> validateOpenQuestionAnswer(SubmissionAnswerRequestDto submissionAnswerRequestDto) {
        val isInvalidOpenAnswer = submissionAnswerRequestDto.openAnswer() == null
                || submissionAnswerRequestDto.openAnswer().isBlank();
        if (isInvalidOpenAnswer) return List.of("Open answer question must have non-blank open answer");

        return List.of();
    }
}

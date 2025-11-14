package format.backend.form.validator;

import format.backend.form.dto.AnswerRequestDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.QuestionType;
import format.backend.upload.service.UploadService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormValidator {

    private final UploadService uploadService;

    public Map<String, List<String>> validate(FormRequestDto form, String userId) {
        val errors = new HashMap<String, List<String>>();

        validateThumbnail(form, userId).ifPresent(e -> errors.put(e.getKey(), e.getValue()));
        validatePassword(form).ifPresent(e -> errors.put(e.getKey(), e.getValue()));
        validateRequiredQuestionsCount(form).ifPresent(e -> errors.put(e.getKey(), e.getValue()));
        validateQuestions(form, userId).forEach(e -> errors.put(e.getKey(), e.getValue()));

        return Collections.unmodifiableMap(errors);
    }

    private Optional<Map.Entry<String, List<String>>> validateThumbnail(FormRequestDto form, String userId) {
        val isThumbnailUploaded = Optional.ofNullable(form.thumbnailKey())
                .map(thumbnailKey -> uploadService.confirmUpload(thumbnailKey, userId))
                .orElse(true);
        if (isThumbnailUploaded) return Optional.empty();

        val message = String.format("Form image with key %s was not found in storage", form.thumbnailKey());
        return Optional.of(Map.entry("thumbnailKey", List.of(message)));
    }

    private Optional<Map.Entry<String, List<String>>> validatePassword(FormRequestDto form) {
        if (!form.status().equals(FormStatus.PRIVATE)) return Optional.empty();

        val isPasswordBlank =
                Optional.ofNullable(form.password()).map(String::isBlank).orElse(true);
        if (!isPasswordBlank) return Optional.empty();

        val message = String.format("Password cannot be blank for form with status '%s'", FormStatus.PRIVATE);
        return Optional.of(Map.entry("password", List.of(message)));
    }

    private Optional<Map.Entry<String, List<String>>> validateRequiredQuestionsCount(FormRequestDto form) {
        val requiredQuestionsCount =
                form.questions().stream().filter(QuestionRequestDto::isRequired).count();
        if (requiredQuestionsCount >= 1) return Optional.empty();

        val message = "Form must have at least one required question";
        return Optional.of(Map.entry("questions", List.of(message)));
    }

    private List<Map.Entry<String, List<String>>> validateQuestions(FormRequestDto form, String userId) {
        val questions = form.questions();
        val errors = new ArrayList<Map.Entry<String, List<String>>>();

        for (var i = 0; i < questions.size(); i++) {
            val question = questions.get(i);

            if (!uploadService.confirmUpload(question.imageKey(), userId)) {
                val message =
                        String.format("Question image with key '%s' was not found in storage", question.imageKey());
                errors.add(Map.entry(String.format("questions[%s].imageKey", i), List.of(message)));
            }

            val correctAnswersCount = question.answers().stream()
                    .filter(AnswerRequestDto::isCorrect)
                    .count();

            if (question.type().equals(QuestionType.SINGLE_CHOICE) && correctAnswersCount != 1)
                errors.add(Map.entry(
                        String.format("questions[%s].answers", i),
                        List.of("Single choice question must have exactly one valid answer")));
            else if (question.type().equals(QuestionType.MULTIPLE_CHOICE) && correctAnswersCount < 1)
                errors.add(Map.entry(
                        String.format("questions[%s].answers", i),
                        List.of("Multiple choice question must have at least one valid answer")));
        }

        return Collections.unmodifiableList(errors);
    }
}

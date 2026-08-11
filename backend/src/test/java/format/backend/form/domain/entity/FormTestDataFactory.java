package format.backend.form.domain.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

public abstract class FormTestDataFactory {

    @Builder(builderMethodName = "createWithDefaults")
    public static FormEntity create(
            String name,
            String slug,
            @Nullable String description,
            FormLanguage language,
            FormStatus status,
            @Nullable String passwordHash,
            @Nullable FormShuffleVariant shuffleVariant,
            @Nullable String thanksMessage,
            Long estimatedDurationSeconds,
            @Nullable String thumbnailKey,
            Boolean allowsQuestionsPreview,
            Boolean allowsGuestSubmissions,
            Boolean saveSubmissions,
            Boolean showAnswersFeedback,
            List<QuestionEntity> questions,
            Integer questionsCount,
            Long submissionsCount,
            Long ratingsCount,
            Long ratingsSum,
            @Nullable String authorId,
            @Nullable Instant createdAt,
            @Nullable Instant updatedAt) {
        name = name != null ? name : "test form";
        slug = slug != null ? slug : "slug-" + UUID.randomUUID();
        language = language != null ? language : FormLanguage.EN;
        status = status != null ? status : FormStatus.PUBLIC;
        estimatedDurationSeconds = estimatedDurationSeconds != null
                ? estimatedDurationSeconds
                : Duration.ofMinutes(1).toSeconds();
        allowsQuestionsPreview = allowsQuestionsPreview == null || allowsQuestionsPreview;
        allowsGuestSubmissions = allowsGuestSubmissions == null || allowsGuestSubmissions;
        saveSubmissions = saveSubmissions == null || saveSubmissions;
        showAnswersFeedback = showAnswersFeedback == null || showAnswersFeedback;
        questions = questions != null && !questions.isEmpty()
                ? questions
                : List.of(
                        QuestionEntity.builder()
                                .content("question A")
                                .type(QuestionType.SINGLE_CHOICE)
                                .isRequired(true)
                                .answers(List.of(
                                        new AnswerEntity("answer A", true), new AnswerEntity("answer B", false)))
                                .build(),
                        QuestionEntity.builder()
                                .content("question B")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .isRequired(true)
                                .answers(List.of(
                                        new AnswerEntity("answer A", true), new AnswerEntity("answer B", false)))
                                .build(),
                        QuestionEntity.builder()
                                .content("question C")
                                .type(QuestionType.OPEN)
                                .isRequired(true)
                                .build());
        questionsCount = questionsCount != null ? questionsCount : questions.size();
        submissionsCount = submissionsCount != null ? submissionsCount : 0;
        ratingsCount = ratingsCount != null ? ratingsCount : 0;
        ratingsSum = ratingsSum != null ? ratingsSum : 0;

        return new FormEntity(
                null,
                name,
                slug,
                description,
                language,
                status,
                passwordHash,
                shuffleVariant,
                thanksMessage,
                estimatedDurationSeconds,
                thumbnailKey,
                allowsQuestionsPreview,
                allowsGuestSubmissions,
                saveSubmissions,
                showAnswersFeedback,
                questions,
                questionsCount,
                submissionsCount,
                ratingsCount,
                ratingsSum,
                authorId,
                createdAt,
                updatedAt,
                0);
    }
}

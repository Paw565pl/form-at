package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.datafactory.FormRequestDtoTestDataFactory;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.domain.entity.QuestionEntity;
import format.backend.form.domain.entity.QuestionType;
import format.backend.submission.domain.entity.SubmissionAnswerEntity;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsTestDataFactory;
import io.restassured.http.ContentType;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormUpdateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "idOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(FormRequestDtoTestDataFactory.createValidPublic())
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(FormRequestDtoTestDataFactory.createValidPublic())
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotTheAuthor() {
        val author = mongoTemplate.save(UserTestDataFactory.create());
        val nonAuthor = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(author.getId())
                .build());

        val token = JwtTestFactory.create(nonAuthor);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(FormRequestDtoTestDataFactory.createValidPublic())
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(Map.of())
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val invalidRequestBody = FormRequestDtoTestDataFactory.createWithInvalidQuestionAnswers();

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnConflictWhenUpdatingToAlreadyExistingName() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .name(requestBody.name())
                .slug(requestBody.name())
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form2.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldUpdateFormAndKeepQuestionIdsWhenQuestionsAreUnchanged() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val originalQuestion = QuestionEntity.builder()
                .content("Original question")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .submissionsCount(10L)
                .questions(List.of(originalQuestion))
                .build());

        val existingSubmission = mongoTemplate.save(SubmissionEntity.builder()
                .formId(existingForm.getId())
                .authorId(user.getId())
                .answer(SubmissionAnswerEntity.forOpenQuestion(originalQuestion.getId(), "Some answer"))
                .build());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(
                existingForm.getId(), Map.of(originalQuestion.getId(), Map.of())));

        val questionDto =
                new QuestionRequestDto(originalQuestion.getContent(), QuestionType.OPEN, null, true, List.of());
        val requestBody = FormRequestDtoTestDataFactory.create(
                FormStatus.PUBLIC, null, List.of(questionDto, questionDto, questionDto));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", is(requestBody.name()));

        val savedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
        assertThat(savedForm).isNotNull();
        assertThat(savedForm.getQuestions()).hasSize(3);
        assertThat(savedForm.getQuestions().getFirst().getId()).isEqualTo(originalQuestion.getId());

        await().pollDelay(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            val updatedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
            assertThat(updatedForm).isNotNull();
            assertThat(updatedForm.getSubmissionsCount()).isEqualTo(10L);

            val submission = mongoTemplate.findById(existingSubmission.getId(), SubmissionEntity.class);
            assertThat(submission).isNotNull();
            assertThat(submission.getAnswers()).hasSize(1);

            val updatedStats = mongoTemplate.findById(existingForm.getId(), SubmissionsStatisticsEntity.class);
            assertThat(updatedStats).isNotNull();
            assertThat(updatedStats.getQuestions()).containsKey(originalQuestion.getId());
        });
    }

    @Test
    void shouldReorderQuestionsWithoutRegeneratingIdsOrTriggeringSubmissionsCleanup() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val question1 = QuestionEntity.builder()
                .content("Question ONE")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val question2 = QuestionEntity.builder()
                .content("Question TWO")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val question3 = QuestionEntity.builder()
                .content("Question THREE")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();

        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .submissionsCount(5L)
                .questions(List.of(question1, question2, question3))
                .build());

        val existingSubmission = mongoTemplate.save(SubmissionEntity.builder()
                .formId(existingForm.getId())
                .authorId(user.getId())
                .answer(SubmissionAnswerEntity.forOpenQuestion(question1.getId(), "Answer one"))
                .answer(SubmissionAnswerEntity.forOpenQuestion(question2.getId(), "Answer two"))
                .answer(SubmissionAnswerEntity.forOpenQuestion(question3.getId(), "Answer three"))
                .build());

        val dto1 = new QuestionRequestDto(question1.getContent(), QuestionType.OPEN, null, true, List.of());
        val dto2 = new QuestionRequestDto(question2.getContent(), QuestionType.OPEN, null, true, List.of());
        val dto3 = new QuestionRequestDto(question3.getContent(), QuestionType.OPEN, null, true, List.of());
        val requestBody = FormRequestDtoTestDataFactory.create(FormStatus.PUBLIC, null, List.of(dto3, dto1, dto2));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        val savedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
        assertThat(savedForm).isNotNull();
        assertThat(savedForm.getQuestions()).hasSize(3);
        assertThat(savedForm.getQuestions().get(0).getId()).isEqualTo(question3.getId());
        assertThat(savedForm.getQuestions().get(1).getId()).isEqualTo(question1.getId());
        assertThat(savedForm.getQuestions().get(2).getId()).isEqualTo(question2.getId());

        await().pollDelay(Duration.ofSeconds(1)).atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            val updatedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
            assertThat(updatedForm).isNotNull();
            assertThat(updatedForm.getSubmissionsCount()).isEqualTo(5L);

            val submission = mongoTemplate.findById(existingSubmission.getId(), SubmissionEntity.class);
            assertThat(submission).isNotNull();
            assertThat(submission.getAnswers()).hasSize(3);
        });
    }

    @Test
    void shouldRegenerateQuestionIdsAndTriggerSubmissionsCleanupWhenQuestionsAreModified() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val oldQuestion = QuestionEntity.builder()
                .content("Old question")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .submissionsCount(1L)
                .questions(List.of(oldQuestion))
                .build());

        val submissionAnswer = SubmissionAnswerEntity.forOpenQuestion(oldQuestion.getId(), "Some answer");
        val existingSubmission = mongoTemplate.save(SubmissionEntity.builder()
                .formId(existingForm.getId())
                .authorId(user.getId())
                .answer(submissionAnswer)
                .build());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(
                existingForm.getId(), Map.of(oldQuestion.getId(), Map.of())));

        val modifiedQuestionDto =
                new QuestionRequestDto("Completely new question", QuestionType.OPEN, null, true, List.of());
        val requestBody = FormRequestDtoTestDataFactory.create(
                FormStatus.PUBLIC, null, List.of(modifiedQuestionDto, modifiedQuestionDto, modifiedQuestionDto));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        val savedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
        assertThat(savedForm).isNotNull();
        assertThat(savedForm.getQuestions()).hasSize(3);

        val newQuestionId = savedForm.getQuestions().getFirst().getId();
        assertThat(newQuestionId).isNotEqualTo(oldQuestion.getId());

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            val submission = mongoTemplate.findById(existingSubmission.getId(), SubmissionEntity.class);
            assertThat(submission).isNull();

            val updatedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
            assertThat(updatedForm).isNotNull();
            assertThat(updatedForm.getSubmissionsCount()).isZero();

            val updatedStats = mongoTemplate.findById(existingForm.getId(), SubmissionsStatisticsEntity.class);
            assertThat(updatedStats).isNotNull();
            assertThat(updatedStats.getQuestions()).doesNotContainKey(oldQuestion.getId());
        });
    }

    @Test
    void shouldRemoveInvalidatedAnswersAndKeepSubmissionWhenQuestionsAreModified() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val unchangedQuestion = QuestionEntity.builder()
                .content("Unchanged question")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val modifiedQuestion1 = QuestionEntity.builder()
                .content("Old question 1")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val modifiedQuestion2 = QuestionEntity.builder()
                .content("Old question 2")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .submissionsCount(1L)
                .questions(List.of(unchangedQuestion, modifiedQuestion1, modifiedQuestion2))
                .build());

        val existingSubmission = mongoTemplate.save(SubmissionEntity.builder()
                .formId(existingForm.getId())
                .authorId(user.getId())
                .answer(SubmissionAnswerEntity.forOpenQuestion(unchangedQuestion.getId(), "Kept answer"))
                .answer(SubmissionAnswerEntity.forOpenQuestion(modifiedQuestion1.getId(), "Lost answer 1"))
                .answer(SubmissionAnswerEntity.forOpenQuestion(modifiedQuestion2.getId(), "Lost answer 2"))
                .build());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(
                existingForm.getId(),
                Map.of(
                        unchangedQuestion.getId(), Map.of(),
                        modifiedQuestion1.getId(), Map.of(),
                        modifiedQuestion2.getId(), Map.of())));

        val unchangedQuestionDto =
                new QuestionRequestDto(unchangedQuestion.getContent(), QuestionType.OPEN, null, true, List.of());
        val modifiedQuestionDto1 = new QuestionRequestDto("New content 1", QuestionType.OPEN, null, true, List.of());
        val modifiedQuestionDto2 = new QuestionRequestDto("New content 2", QuestionType.OPEN, null, true, List.of());
        val requestBody = FormRequestDtoTestDataFactory.create(
                FormStatus.PUBLIC, null, List.of(unchangedQuestionDto, modifiedQuestionDto1, modifiedQuestionDto2));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            val submission = mongoTemplate.findById(existingSubmission.getId(), SubmissionEntity.class);
            assertThat(submission).isNotNull();

            assertThat(submission.getAnswers()).hasSize(1);
            assertThat(submission.getAnswers().getFirst().getQuestionId()).isEqualTo(unchangedQuestion.getId());

            val updatedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
            assertThat(updatedForm).isNotNull();
            assertThat(updatedForm.getSubmissionsCount()).isEqualTo(1L);

            val updatedStats = mongoTemplate.findById(existingForm.getId(), SubmissionsStatisticsEntity.class);
            assertThat(updatedStats).isNotNull();
            assertThat(updatedStats.getQuestions()).containsKey(unchangedQuestion.getId());
            assertThat(updatedStats.getQuestions()).doesNotContainKey(modifiedQuestion1.getId());
            assertThat(updatedStats.getQuestions()).doesNotContainKey(modifiedQuestion2.getId());
        });
    }

    @Test
    void shouldKeepUniqueQuestionIdsWhenFormContainsDuplicateQuestions() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val duplicateContent = "What is your name?";
        val duplicateQ1 = QuestionEntity.builder()
                .content(duplicateContent)
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val duplicateQ2 = QuestionEntity.builder()
                .content(duplicateContent)
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();
        val distinctQ3 = QuestionEntity.builder()
                .content("What is your age?")
                .type(QuestionType.OPEN)
                .isRequired(true)
                .build();

        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .submissionsCount(2L)
                .questions(List.of(duplicateQ1, duplicateQ2, distinctQ3))
                .build());

        val duplicateDto = new QuestionRequestDto(duplicateContent, QuestionType.OPEN, null, true, List.of());
        val distinctDto = new QuestionRequestDto(distinctQ3.getContent(), QuestionType.OPEN, null, true, List.of());

        val requestBody = FormRequestDtoTestDataFactory.create(
                FormStatus.PUBLIC, null, List.of(duplicateDto, duplicateDto, distinctDto));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value());

        val savedForm = mongoTemplate.findById(existingForm.getId(), FormEntity.class);
        assertThat(savedForm).isNotNull();
        assertThat(savedForm.getQuestions()).hasSize(3);

        val newQ1Id = savedForm.getQuestions().get(0).getId();
        val newQ2Id = savedForm.getQuestions().get(1).getId();
        val newQ3Id = savedForm.getQuestions().get(2).getId();

        assertThat(newQ1Id).isEqualTo(duplicateQ1.getId());
        assertThat(newQ2Id).isEqualTo(duplicateQ2.getId());
        assertThat(newQ3Id).isEqualTo(distinctQ3.getId());
        assertThat(newQ1Id).isNotEqualTo(newQ2Id);
    }
}

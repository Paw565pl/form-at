package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.domain.entity.QuestionEntity;
import format.backend.form.domain.entity.QuestionType;
import format.backend.submission.application.shared.dto.SubmissionAnswerRequestDto;
import format.backend.submission.application.shared.dto.SubmissionRequestDto;
import format.backend.submission.datafactory.SubmissionRequestDtoTestDataFactory;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

final class SubmissionCreateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/submissions".formatted(FORM_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenGuestSubmitsToFormThatRequiresLogin() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .allowsGuestSubmissions(false)
                .build());

        given().pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValid(form))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValid(form))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnConflictWhenFormDoesNotSaveSubmissions() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build();
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValid(form))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnConflictWhenFormHasNoAuthor() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(null)
                .build());

        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValid(form))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnConflictWhenUserSubmitsTwiceToSameForm() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val submitter = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValid(form))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnBadRequestWhenAnswersListIsEmpty() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.create(List.of()))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenRequiredQuestionIsMissing() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val validSubmission = SubmissionRequestDtoTestDataFactory.createValid(form);
        val incompleteSubmission = SubmissionRequestDtoTestDataFactory.create(
                List.of(validSubmission.answers().getFirst()));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(incompleteSubmission)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenQuestionDoesNotExistInForm() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val invalidAnswer = new SubmissionAnswerRequestDto(
                ObjectId.get().toHexString(), Set.of(ObjectId.get().toHexString()), null);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.create(List.of(invalidAnswer)))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenSingleChoiceQuestionHasMultipleAnswers() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val invalidAnswer = new SubmissionAnswerRequestDto(
                singleChoiceQuestion.getId(), Set.of(ObjectId.get().toHexString(), new ObjectId().toHexString()), null);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.create(List.of(invalidAnswer)))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenOpenQuestionIsBlank() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val openQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.OPEN))
                .findFirst()
                .orElseThrow();

        val invalidAnswer = new SubmissionAnswerRequestDto(openQuestion.getId(), Set.of(), " ".repeat(20));

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.create(List.of(invalidAnswer)))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenSingleChoiceQuestionHasInvalidAnswerId() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val invalidAnswer = new SubmissionAnswerRequestDto(
                singleChoiceQuestion.getId(), Set.of(ObjectId.get().toHexString()), null);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValidWithOverriddenAnswer(form, invalidAnswer))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenMultipleChoiceQuestionHasEmptyAnswers() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val multipleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.MULTIPLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val invalidAnswer = new SubmissionAnswerRequestDto(multipleChoiceQuestion.getId(), Set.of(), null);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValidWithOverriddenAnswer(form, invalidAnswer))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenMultipleChoiceQuestionHasInvalidAnswerId() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val multipleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.MULTIPLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val invalidAnswer = new SubmissionAnswerRequestDto(
                multipleChoiceQuestion.getId(), Set.of(ObjectId.get().toHexString()), null);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValidWithOverriddenAnswer(form, invalidAnswer))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldSanitizeSubmissionAndClearOpenAnswerForChoiceQuestions() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();

        val sneakyAnswer = new SubmissionAnswerRequestDto(
                singleChoiceQuestion.getId(),
                Set.of(singleChoiceQuestion.getAnswers().getFirst().getId()),
                "test open answer");

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValidWithOverriddenAnswer(form, sneakyAnswer))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        val savedSubmission = mongoTemplate.findAll(SubmissionEntity.class).getFirst();
        val savedAnswer = savedSubmission.getAnswers().stream()
                .filter(a -> a.getQuestionId().equals(singleChoiceQuestion.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(savedAnswer.getOpenAnswer()).isNull();
    }

    @Test
    void shouldSanitizeSubmissionAndClearChosenAnswersForOpenQuestions() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val openQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.OPEN))
                .findFirst()
                .orElseThrow();
        val sneakyAnswer = new SubmissionAnswerRequestDto(
                openQuestion.getId(), Set.of(ObjectId.get().toHexString()), "test open answer");

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(SubmissionRequestDtoTestDataFactory.createValidWithOverriddenAnswer(form, sneakyAnswer))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        val savedSubmission = mongoTemplate.findAll(SubmissionEntity.class).getFirst();
        val savedAnswer = savedSubmission.getAnswers().stream()
                .filter(a -> a.getQuestionId().equals(openQuestion.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(savedAnswer.getChosenAnswerIds()).isEmpty();
    }

    @Test
    void shouldCreateSubmissionSuccessfullyForAuthenticatedUser() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(requestDto)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("authorName", is(submitter.getUsername()));

        verifyDbState(form.getId(), submitter.getId(), requestDto);
    }

    @Test
    void shouldCreateSubmissionSuccessfullyForGuestWhenGuestSubmissionsAllowed() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build();
        form.setAllowsGuestSubmissions(true);
        mongoTemplate.save(form);

        val requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

        given().pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(requestDto)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("authorName", nullValue());

        verifyDbState(form.getId(), null, requestDto);
    }

    @Test
    void shouldResolveFormSlugAndCreateSubmission() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .contentType(ContentType.JSON)
                .body(requestDto)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());

        verifyDbState(form.getId(), ownerUser.getId(), requestDto);
    }

    @Test
    void shouldCreateSubmissionSuccessfullyWhenOptionalQuestionIsOmitted() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .questions(List.of(
                        QuestionEntity.builder()
                                .content("question")
                                .type(QuestionType.OPEN)
                                .isRequired(false)
                                .build(),
                        QuestionEntity.builder()
                                .content("question")
                                .type(QuestionType.OPEN)
                                .isRequired(true)
                                .build()))
                .build());

        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val validRequest = SubmissionRequestDtoTestDataFactory.createValid(form);
        val answersWithoutOptional = validRequest.answers().stream()
                .filter(a ->
                        !a.questionId().equals(form.getQuestions().getFirst().getId()))
                .toList();
        val requestWithoutOptional = SubmissionRequestDtoTestDataFactory.create(answersWithoutOptional);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(requestWithoutOptional)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        verifyDbState(form.getId(), submitter.getId(), requestWithoutOptional);
    }

    private void verifyDbState(String expectedFormId, String expectedAuthorId, SubmissionRequestDto executedRequest) {
        val savedSubmissions = mongoTemplate.findAll(SubmissionEntity.class);
        assertThat(savedSubmissions).hasSize(1);

        val savedSubmission = savedSubmissions.getFirst();
        assertThat(savedSubmission.getFormId()).isEqualTo(expectedFormId);
        assertThat(savedSubmission.getAuthorId()).isEqualTo(expectedAuthorId);
        assertThat(savedSubmission.getAnswers())
                .hasSize(executedRequest.answers().size());

        val updatedForm = mongoTemplate.findById(expectedFormId, FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getSubmissionsCount()).isEqualTo(1L);

        val stats = mongoTemplate.findOne(
                Query.query(
                        Criteria.where(SubmissionsStatisticsEntity::getFormId).is(expectedFormId)),
                SubmissionsStatisticsEntity.class);
        assertThat(stats).isNotNull();

        for (val answer : executedRequest.answers()) {
            if (answer.chosenAnswerIds() != null && !answer.chosenAnswerIds().isEmpty()) {
                assertThat(stats.getQuestions()).containsKey(answer.questionId());

                val questionStats = stats.getQuestions().get(answer.questionId());
                for (val chosenAnswerId : answer.chosenAnswerIds()) {
                    assertThat(questionStats.getAnswers()).containsEntry(chosenAnswerId, 1L);
                }
            }
        }
    }
}

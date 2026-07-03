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
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.QuestionType;
import format.backend.submission.datafactory.SubmissionRequestDtoTestDataFactory;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.dto.SubmissionAnswerRequestDto;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

class SubmissionCreateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/submissions".formatted(FORM_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenGuestSubmitsToFormThatRequiresLogin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setAllowsGuestSubmissions(false);
        mongoTemplate.save(form);

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user.getId()));

        var token = JwtTestFactory.create(user);
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        var token = JwtTestFactory.create(ownerUser);
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
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, null));

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(submitter);
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var submitter = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        var token = JwtTestFactory.create(submitter);
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var validSubmission = SubmissionRequestDtoTestDataFactory.createValid(form);
        var incompleteSubmission = SubmissionRequestDtoTestDataFactory.create(
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var invalidAnswer = new SubmissionAnswerRequestDto(
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        var invalidAnswer = new SubmissionAnswerRequestDto(
                singleChoiceQuestion.getId(),
                Set.of(ObjectId.get().toHexString(), ObjectId.get().toHexString()),
                null);

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var openQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.OPEN))
                .findFirst()
                .orElseThrow();

        var invalidAnswer = new SubmissionAnswerRequestDto(openQuestion.getId(), Set.of(), "   ");

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        var invalidAnswer = new SubmissionAnswerRequestDto(
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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var multipleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.MULTIPLE_CHOICE))
                .findFirst()
                .orElseThrow();
        var invalidAnswer = new SubmissionAnswerRequestDto(multipleChoiceQuestion.getId(), Set.of(), null);

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();

        var sneakyAnswer = new SubmissionAnswerRequestDto(
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

        var savedSubmission = mongoTemplate.findAll(SubmissionEntity.class).getFirst();
        var savedAnswer = savedSubmission.getAnswers().stream()
                .filter(a -> a.getQuestionId().equals(singleChoiceQuestion.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(savedAnswer.getOpenAnswer()).isNull();
    }

    @Test
    void shouldSanitizeSubmissionAndClearChosenAnswersForOpenQuestions() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var openQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.OPEN))
                .findFirst()
                .orElseThrow();
        var sneakyAnswer = new SubmissionAnswerRequestDto(
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

        var savedSubmission = mongoTemplate.findAll(SubmissionEntity.class).getFirst();
        var savedAnswer = savedSubmission.getAnswers().stream()
                .filter(a -> a.getQuestionId().equals(openQuestion.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(savedAnswer.getChosenAnswerIds()).isEmpty();
    }

    @Test
    void shouldCreateSubmissionSuccessfullyForAuthenticatedUser() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setAllowsGuestSubmissions(true);
        mongoTemplate.save(form);

        var requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestDto = SubmissionRequestDtoTestDataFactory.createValid(form);

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
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        var optionalQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.OPEN))
                .findFirst()
                .orElseThrow();
        optionalQuestion.setIsRequired(false);
        mongoTemplate.save(form);

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(submitter);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var validRequest = SubmissionRequestDtoTestDataFactory.createValid(form);
        var answersWithoutOptional = validRequest.answers().stream()
                .filter(a -> !a.questionId().equals(optionalQuestion.getId()))
                .toList();
        var requestWithoutOptional = SubmissionRequestDtoTestDataFactory.create(answersWithoutOptional);

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
        var savedSubmissions = mongoTemplate.findAll(SubmissionEntity.class);
        assertThat(savedSubmissions).hasSize(1);

        var savedSubmission = savedSubmissions.getFirst();
        assertThat(savedSubmission.getFormId()).isEqualTo(expectedFormId);
        assertThat(savedSubmission.getAuthorId()).isEqualTo(expectedAuthorId);
        assertThat(savedSubmission.getAnswers())
                .hasSize(executedRequest.answers().size());

        var updatedForm = mongoTemplate.findById(expectedFormId, FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getSubmissionsCount()).isEqualTo(1L);

        var stats = mongoTemplate.findOne(
                Query.query(
                        Criteria.where(SubmissionsStatisticsEntity::getFormId).is(expectedFormId)),
                SubmissionsStatisticsEntity.class);
        assertThat(stats).isNotNull();

        for (var answer : executedRequest.answers()) {
            if (answer.chosenAnswerIds() != null && !answer.chosenAnswerIds().isEmpty()) {
                assertThat(stats.getQuestions()).containsKey(answer.questionId());

                var questionStats = stats.getQuestions().get(answer.questionId());
                for (var chosenAnswerId : answer.chosenAnswerIds()) {
                    assertThat(questionStats.getAnswers()).containsEntry(chosenAnswerId, 1L);
                }
            }
        }
    }
}

package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.Role;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.entity.SubmissionAnswerEntity;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SubmissionRetrieveIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String SUBMISSION_PATH_PARAM = "submissionId";
    private static final String PATH =
            "/api/v1/forms/{%s}/submissions/{%s}".formatted(FORM_PATH_PARAM, SUBMISSION_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenSubmissionDoesNotExist() {
        var owner = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));

        var token = JwtTestFactory.create(owner);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenSubmissionBelongsToAnotherForm() {
        var owner = mongoTemplate.save(UserTestDataFactory.create());

        var form1 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));
        var form2 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));

        var submissionOnForm1 =
                mongoTemplate.save(SubmissionTestDataFactory.create(form1.getId(), owner.getId(), List.of()));

        var token = JwtTestFactory.create(owner);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form2.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submissionOnForm1.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNeitherOwnerNorAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var attackerToken = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(attackerToken);

        given().auth()
                .oauth2(attackerToken.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnConflictWhenFormDoesNotSaveSubmissions() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnSubmissionSuccessfullyWhenUserIsOwner() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var submitter = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(submission.getId()))
                .body("authorName", is(submitter.getUsername()))
                .body("answers", hasSize(0));
    }

    @Test
    void shouldReturnSubmissionSuccessfullyWhenUserIsAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var admin = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var adminToken = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(adminToken);

        given().auth()
                .oauth2(adminToken.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(submission.getId()));
    }

    @Test
    void shouldReturnSubmissionWithNullAuthorNameWhenSubmitterIsAnonymous() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submission = mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), null, List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(submission.getId()))
                .body("authorName", nullValue());
    }

    @Test
    void shouldReturnSubmissionAndMapOpenAnswerToNullForClosedQuestions() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submissionAnswer = new SubmissionAnswerEntity(ObjectId.get().toHexString());
        var submission = mongoTemplate.save(
                SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of(submissionAnswer)));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(submission.getId()))
                .body("answers", hasSize(1))
                .body("answers[0].openAnswer", nullValue())
                .body("answers[0].chosenAnswerIds", hasSize(0));
    }

    @Test
    void shouldResolveFormSlugAndReturnSubmission() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(submission.getId()));
    }
}

package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.Role;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.QuestionType;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.datafactory.SubmissionsStatisticsTestDataFactory;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

class SubmissionDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String SUBMISSION_PATH_PARAM = "submissionId";
    private static final String PATH =
            "/api/v1/forms/{%s}/submissions/{%s}".formatted(FORM_PATH_PARAM, SUBMISSION_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
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
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenSubmissionDoesNotExist() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenSubmissionBelongsToAnotherForm() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form1 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        var form2 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form1.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form2.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
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

        var token = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
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
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnConflictWhenFormHasNoAuthor() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, null));
        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        var token = JwtTestFactory.create(submitter, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldDeleteSubmissionSuccessfullyWhenUserIsOwner() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        mongoTemplate.save(form);

        var submitter = mongoTemplate.save(UserTestDataFactory.create());
        var submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, submitter.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verifyDbState(form, submission.getId());
    }

    @Test
    void shouldDeleteSubmissionSuccessfullyWhenUserIsAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        mongoTemplate.save(form);

        var submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, ownerUser.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var adminUser = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(adminUser, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verifyDbState(form, submission.getId());
    }

    @Test
    void shouldResolveFormSlugAndDeleteSubmissionSuccessfully() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        mongoTemplate.save(form);

        var submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, ownerUser.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .pathParam(SUBMISSION_PATH_PARAM, submission.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verifyDbState(form, submission.getId());
    }

    private void verifyDbState(FormEntity form, String deletedSubmissionId) {
        var deletedSubmission = mongoTemplate.findById(deletedSubmissionId, SubmissionEntity.class);
        assertThat(deletedSubmission).isNull();

        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getSubmissionsCount()).isZero();

        var stats = mongoTemplate.findOne(
                Query.query(
                        Criteria.where(SubmissionsStatisticsEntity::getFormId).is(form.getId())),
                SubmissionsStatisticsEntity.class);
        assertThat(stats).isNotNull();

        for (var question : form.getQuestions()) {
            if (question.getType() == QuestionType.OPEN) continue;

            assertThat(stats.getQuestions()).containsKey(question.getId());
            var questionStats = stats.getQuestions().get(question.getId());

            for (var answer : question.getAnswers()) {
                assertThat(questionStats.getAnswers()).containsEntry(answer.getId(), 0L);
            }
        }
    }
}

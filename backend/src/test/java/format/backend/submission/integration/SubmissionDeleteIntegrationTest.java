package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.Role;
import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.domain.entity.QuestionType;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsTestDataFactory;
import java.util.List;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

final class SubmissionDeleteIntegrationTest extends BaseIntegrationTest {

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
                .pathParam(SUBMISSION_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenSubmissionBelongsToAnotherForm() {
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form1 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form1.getId(), ownerUser.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        val submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        val token = JwtTestFactory.create(otherUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build();
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        val submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(null)
                .build());
        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        val token = JwtTestFactory.create(submitter, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submitter = mongoTemplate.save(UserTestDataFactory.create());
        val submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, submitter.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, ownerUser.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val adminUser = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(adminUser, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = mongoTemplate.save(SubmissionTestDataFactory.createValid(form, ownerUser.getId()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val token = JwtTestFactory.create(ownerUser);
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
        val deletedSubmission = mongoTemplate.findById(deletedSubmissionId, SubmissionEntity.class);
        assertThat(deletedSubmission).isNull();

        val updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getSubmissionsCount()).isZero();

        val stats = mongoTemplate.findOne(
                Query.query(
                        Criteria.where(SubmissionsStatisticsEntity::getFormId).is(form.getId())),
                SubmissionsStatisticsEntity.class);
        assertThat(stats).isNotNull();

        for (val question : form.getQuestions()) {
            if (question.getType() == QuestionType.OPEN) continue;

            assertThat(stats.getQuestions()).containsKey(question.getId());
            val questionStats = stats.getQuestions().get(question.getId());

            for (val answer : question.getAnswers()) {
                assertThat(questionStats.getAnswers()).containsEntry(answer.getId(), 0L);
            }
        }
    }
}

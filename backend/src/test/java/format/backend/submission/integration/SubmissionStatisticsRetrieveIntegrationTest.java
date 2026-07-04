package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.Role;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.QuestionType;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.datafactory.SubmissionsStatisticsTestDataFactory;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SubmissionStatisticsRetrieveIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/submissions/statistics".formatted(FORM_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
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
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenFormSlugDoesNotExist() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, "non-existing-slug")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotFormOwner() {
        var ownerUserUser = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUserUser.getId());
        mongoTemplate.save(form);

        var token = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
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

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnConflictWhenFormHasNoAuthor() {
        var admin = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create();
        form.setAuthorId(null);
        mongoTemplate.save(form);

        var token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnStatisticsByFormId() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        form = mongoTemplate.save(form);

        var submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("submissionsCount", is(1))
                .body("questions.size()", greaterThan(0));
    }

    @Test
    void shouldReturnStatisticsByFormSlug() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        form = mongoTemplate.save(form);

        var submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("submissionsCount", is(1))
                .body("questions.size()", greaterThan(0));
    }

    @Test
    void shouldReturnStatisticsForAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var admin = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        form = mongoTemplate.save(form);

        var submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        var token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("submissionsCount", is(1))
                .body("questions.size()", greaterThan(0));
    }

    @Test
    void shouldReturnSubmissionsCountFromForm() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(15L);
        form = mongoTemplate.save(form);

        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(form.getId(), Map.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("submissionsCount", is(15));
    }

    @Test
    void shouldReturnAnswersStatisticsCorrectly() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSubmissionsCount(1L);
        form = mongoTemplate.save(form);

        var submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        var statistics = SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission);
        mongoTemplate.save(statistics);

        var singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        var singleChoiceAnswerA = singleChoiceQuestion.getAnswers().getFirst();
        var singleChoiceAnswerB = singleChoiceQuestion.getAnswers().get(1);

        var multipleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.MULTIPLE_CHOICE))
                .findFirst()
                .orElseThrow();
        var multipleChoiceAnswerA = multipleChoiceQuestion.getAnswers().getFirst();
        var multipleChoiceAnswerB = multipleChoiceQuestion.getAnswers().get(1);

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("submissionsCount", is(1))
                .body(
                        "questions.%s.answers.%s".formatted(singleChoiceQuestion.getId(), singleChoiceAnswerA.getId()),
                        is(1))
                .body(
                        "questions.%s.answers.%s".formatted(singleChoiceQuestion.getId(), singleChoiceAnswerB.getId()),
                        is(0))
                .body(
                        "questions.%s.answers.%s"
                                .formatted(multipleChoiceQuestion.getId(), multipleChoiceAnswerA.getId()),
                        is(1))
                .body(
                        "questions.%s.answers.%s"
                                .formatted(multipleChoiceQuestion.getId(), multipleChoiceAnswerB.getId()),
                        is(1));
    }
}

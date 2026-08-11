package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.Role;
import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.domain.entity.QuestionType;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.domain.entity.SubmissionsStatisticsTestDataFactory;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class SubmissionStatisticsRetrieveIntegrationTest extends BaseIntegrationTest {

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        ;

        val token = JwtTestFactory.create(otherUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .saveSubmissions(false)
                .build());

        val token = JwtTestFactory.create(ownerUser);
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
        val admin = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().authorId(null).build());

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val admin = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission));

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(15L)
                .build());
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(form.getId(), Map.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .submissionsCount(1L)
                .build());
        val submission = SubmissionTestDataFactory.createValid(form, ownerUser.getId());
        val statistics = SubmissionsStatisticsTestDataFactory.createInitializedWithSubmission(form, submission);
        mongoTemplate.save(statistics);

        val singleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.SINGLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val singleChoiceAnswerA = singleChoiceQuestion.getAnswers().getFirst();
        val singleChoiceAnswerB = singleChoiceQuestion.getAnswers().get(1);

        val multipleChoiceQuestion = form.getQuestions().stream()
                .filter(q -> q.getType().equals(QuestionType.MULTIPLE_CHOICE))
                .findFirst()
                .orElseThrow();
        val multipleChoiceAnswerA = multipleChoiceQuestion.getAnswers().getFirst();
        val multipleChoiceAnswerB = multipleChoiceQuestion.getAnswers().get(1);

        val token = JwtTestFactory.create(ownerUser);
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

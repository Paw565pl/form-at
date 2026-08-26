package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.domain.entity.UserEntity;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.datafactory.FormRequestDtoTestDataFactory;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.domain.entity.QuestionType;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormCreateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/forms";

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().contentType(ContentType.JSON)
                .body(FormRequestDtoTestDataFactory.createValidPublic())
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(Map.of())
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenNoRequiredQuestions() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val optionalQuestion = new QuestionRequestDto("question", QuestionType.OPEN, null, false, List.of());
        val request = FormRequestDtoTestDataFactory.create(
                FormStatus.PUBLIC, null, List.of(optionalQuestion, optionalQuestion, optionalQuestion));

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenMissingCorrectAnswer() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createWithInvalidQuestionAnswers();

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenPrivateFormWithoutPassword() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createValidPrivate(null);

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnConflictWhenFormSlugAlreadyExists() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createValidPublic();
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .slug(requestBody.name())
                .build());

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldCreatePublic() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", is(requestBody.name()))
                .body("authorName", is(user.getUsername()))
                .body("status", is(FormStatus.PUBLIC.name()))
                .body("submissionsCount", is(0))
                .body("questions", hasSize(requestBody.questions().size()))
                .body(
                        "questions[0].answers",
                        hasSize(requestBody.questions().getFirst().answers().size()));

        verifyDbState(user, requestBody);
    }

    @Test
    void shouldCreatePrivate() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createValidPrivate("password");

        given().auth()
                .oauth2(token.getTokenValue())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", is(requestBody.name()))
                .body("authorName", is(user.getUsername()))
                .body("status", is(FormStatus.PRIVATE.name()))
                .body("submissionsCount", is(0));

        verifyDbState(user, requestBody);
    }

    private void verifyDbState(UserEntity user, FormRequestDto requestBody) {
        val savedForms = mongoTemplate.findAll(FormEntity.class);
        assertThat(savedForms).hasSize(1);

        val savedForm = savedForms.getFirst();
        assertThat(savedForm.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedForm.getQuestionsCount())
                .isEqualTo(requestBody.questions().size());
        assertThat(savedForm.getQuestions()).hasSize(requestBody.questions().size());
        assertThat(savedForm.getSubmissionsCount()).isZero();
        assertThat(savedForm.getRatingsCount()).isZero();
        assertThat(savedForm.getRatingsSum()).isZero();
    }
}

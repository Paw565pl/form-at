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
import format.backend.auth.entity.UserEntity;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormRequestDtoTestDataFactory;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FormCreateIntegrationTest extends BaseIntegrationTest {

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
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
    void shouldReturnBadRequestWhenMissingCorrectAnswer() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createWithInvalidQuestionAnswers();

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createValidPrivate(null);

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        var existingForm = FormTestDataFactory.create();
        existingForm.setSlug(requestBody.name());
        mongoTemplate.save(existingForm);

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createValidPublic();

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createValidPrivate("password");

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
        var savedForms = mongoTemplate.findAll(FormEntity.class);
        assertThat(savedForms).hasSize(1);

        var savedForm = savedForms.getFirst();
        assertThat(savedForm.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedForm.getQuestionsCount())
                .isEqualTo(requestBody.questions().size());
        assertThat(savedForm.getQuestions()).hasSize(requestBody.questions().size());
        assertThat(savedForm.getSubmissionsCount()).isZero();
        assertThat(savedForm.getRatingsCount()).isZero();
        assertThat(savedForm.getRatingsSum()).isZero();
    }
}

package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormRequestDtoTestDataFactory;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import io.restassured.http.ContentType;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FormUpdateIntegrationTest extends BaseIntegrationTest {

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var token = JwtTestFactory.create(user);
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
        var author = mongoTemplate.save(UserTestDataFactory.create());
        var nonAuthor = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, author.getId()));

        var token = JwtTestFactory.create(nonAuthor);
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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user.getId()));

        var token = JwtTestFactory.create(user);
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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var invalidRequestBody = FormRequestDtoTestDataFactory.createWithInvalidQuestionAnswers();

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
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, user.getId());
        form.setName(requestBody.name());
        form.setSlug(requestBody.name());
        mongoTemplate.save(form);

        var form2 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user.getId()));

        var token = JwtTestFactory.create(user);
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
    void shouldUpdateFormAndResetSubmissionsCount() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var existingForm = FormTestDataFactory.create(FormStatus.PUBLIC, user.getId());
        existingForm.setSubmissionsCount(10L);
        mongoTemplate.save(existingForm);

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, existingForm.getId())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(existingForm.getId()))
                .body("name", is(requestBody.name()))
                .body("submissionsCount", is(0));

        var savedForms = mongoTemplate.findAll(FormEntity.class);
        assertThat(savedForms).hasSize(1);

        var savedForm = savedForms.getFirst();
        assertThat(savedForm.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedForm.getQuestionsCount())
                .isEqualTo(requestBody.questions().size());
        assertThat(savedForm.getQuestions()).hasSize(requestBody.questions().size());
        assertThat(savedForm.getSubmissionsCount()).isEqualTo(0);
    }
}

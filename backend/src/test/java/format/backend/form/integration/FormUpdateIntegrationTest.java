package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.datafactory.FormRequestDtoTestDataFactory;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import io.restassured.http.ContentType;
import java.util.Map;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormUpdateIntegrationTest extends BaseIntegrationTest {

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
        val author = mongoTemplate.save(UserTestDataFactory.create());
        val nonAuthor = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(author.getId())
                .build());

        val token = JwtTestFactory.create(nonAuthor);
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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val invalidRequestBody = FormRequestDtoTestDataFactory.createWithInvalidQuestionAnswers();

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val requestBody = FormRequestDtoTestDataFactory.createValidPublic();

        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .name(requestBody.name())
                .slug(requestBody.name())
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val existingForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .submissionsCount(10L)
                .authorId(user.getId())
                .build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val requestBody = FormRequestDtoTestDataFactory.createValidPublic();

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

        val savedForms = mongoTemplate.findAll(FormEntity.class);
        assertThat(savedForms).hasSize(1);

        val savedForm = savedForms.getFirst();
        assertThat(savedForm.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedForm.getQuestionsCount())
                .isEqualTo(requestBody.questions().size());
        assertThat(savedForm.getQuestions()).hasSize(requestBody.questions().size());
        assertThat(savedForm.getSubmissionsCount()).isZero();
    }
}

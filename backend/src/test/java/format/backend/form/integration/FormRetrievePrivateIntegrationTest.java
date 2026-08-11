package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.application.access.AccessPrivateFormRequestDto;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.rating.datafactory.FormRatingTestDataFactory;
import io.restassured.http.ContentType;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

final class FormRetrievePrivateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "idOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/access".formatted(PATH_PARAM);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsMissing() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PRIVATE)
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto(""))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenFormIsNotPrivate() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnForbiddenWhenPasswordIsIncorrect() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PRIVATE)
                .passwordHash(passwordEncoder.encode("correct-password"))
                .build());
        ;

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnOkAndFormDetailsWhenPasswordIsCorrect() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PRIVATE)
                .passwordHash(passwordEncoder.encode("password"))
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnOkAndIncludeUserRatingWhenAuthenticatedUserProvidesCorrectPassword() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PRIVATE)
                .passwordHash(passwordEncoder.encode("password"))
                .build());
        val rating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new AccessPrivateFormRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()))
                .body("userRating", is(rating.getValue()));
    }
}

package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.dto.FormAccessRequestDto;
import format.backend.form.entity.FormStatus;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import io.restassured.http.ContentType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class FormRetrievePrivateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "idOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/access".formatted(PATH_PARAM);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsMissing() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PRIVATE));

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto(""))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnBadRequestWhenFormIsNotPrivate() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnForbiddenWhenPasswordIsIncorrect() {
        var form = FormTestDataFactory.create(FormStatus.PRIVATE);
        form.setPasswordHash(passwordEncoder.encode("correct-password"));
        mongoTemplate.save(form);

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnOkAndFormDetailsWhenPasswordIsCorrect() {
        var form = FormTestDataFactory.create(FormStatus.PRIVATE);
        form.setPasswordHash(passwordEncoder.encode("password"));
        mongoTemplate.save(form);

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnOkAndIncludeUserRatingWhenAuthenticatedUserProvidesCorrectPassword() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PRIVATE);
        form.setPasswordHash(passwordEncoder.encode("password"));
        mongoTemplate.save(form);

        var rating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormAccessRequestDto("password"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()))
                .body("userRating", is(rating.getValue()));
    }
}

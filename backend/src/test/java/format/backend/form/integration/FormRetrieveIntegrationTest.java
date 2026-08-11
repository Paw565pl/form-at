package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.rating.datafactory.FormRatingTestDataFactory;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormRetrieveIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "idOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}".formatted(PATH_PARAM);

    @Test
    void shouldReturnNotFoundWhenFormDoesNotExist() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldFindById() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()))
                .body("slug", is(form.getSlug()))
                .body("name", is(form.getName()))
                .body("questions", hasSize(form.getQuestions().size()))
                .body(
                        "questions[0].answers",
                        hasSize(form.getQuestions().getFirst().getAnswers().size()));
    }

    @Test
    void shouldFindBySlug() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().pathParam(PATH_PARAM, form.getSlug())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()))
                .body("slug", is(form.getSlug()))
                .body("name", is(form.getName()))
                .body("questions", hasSize(form.getQuestions().size()))
                .body(
                        "questions[0].answers",
                        hasSize(form.getQuestions().getFirst().getAnswers().size()));
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingClosedFormAnonymously() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .build());
        given().pathParam(PATH_PARAM, form.getId()).when().get(PATH).then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnForbiddenWhenAccessingClosedFormAsNonAuthor() {
        val author = mongoTemplate.save(UserTestDataFactory.create());
        val nonAuthor = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .authorId(author.getId())
                .build());

        val token = JwtTestFactory.create(nonAuthor);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnOkWhenAccessingClosedFormAsAuthor() {
        val author = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .authorId(author.getId())
                .build());

        val token = JwtTestFactory.create(author);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnOkWhenAccessingPublicFormAnonymously() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnOkWhenAccessingUnpublicFormAnonymously() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.UNPUBLIC)
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnFormWithoutUserRatingForAnonymousUser() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .build());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("userRating", nullValue());
    }

    @Test
    void shouldReturnFormWithUserRatingWhenAuthenticatedUserRatedIt() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .build());
        val rating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()))
                .body("userRating", is(rating.getValue()));
    }
}

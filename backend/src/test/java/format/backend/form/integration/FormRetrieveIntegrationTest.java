package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FormRetrieveIntegrationTest extends BaseIntegrationTest {

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
        var form = mongoTemplate.save(FormTestDataFactory.create());

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
        var form = mongoTemplate.save(FormTestDataFactory.create());

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
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED));

        given().pathParam(PATH_PARAM, form.getId()).when().get(PATH).then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnForbiddenWhenAccessingClosedFormAsNonAuthor() {
        var author = mongoTemplate.save(UserTestDataFactory.create());
        var nonAuthor = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED, author.getId()));

        var token = JwtTestFactory.create(nonAuthor);
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
        var author = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED, author.getId()));

        var token = JwtTestFactory.create(author);
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
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnOkWhenAccessingUnPublicFormAnonymously() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.UNPUBLIC));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(form.getId()));
    }

    @Test
    void shouldReturnFormWithoutUserRatingForAnonymousUser() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("userRating", nullValue());
    }

    @Test
    void shouldReturnFormWithUserRatingWhenAuthenticatedUserRatedIt() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var rating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        var token = JwtTestFactory.create(user);
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

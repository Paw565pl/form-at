package format.backend.comment.datafactory.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.comment.datafactory.datafactory.CommentTestDataFactory;
import format.backend.comment_rating.datafactory.CommentRatingTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CommentListIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/comments".formatted(PATH_PARAM);

    @Test
    void shouldReturnEmptyListWhenFormDoesNotExist() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(0));
    }

    @Test
    void shouldPaginateResults() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "test comment 1"));
        mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "test comment 2"));
        mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "test comment 3"));

        given().pathParam(PATH_PARAM, form.getId())
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(3))
                .body("page.totalPages", is(2))
                .body("content", hasSize(2));
    }

    @Test
    void shouldSortByCreatedAtDescendingByDefault() {
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var olderComment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "older comment"));
        var newerComment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "newer comment"));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(newerComment.getId()))
                .body("content[1].id", is(olderComment.getId()));
    }

    @Test
    void shouldHaveCorrectAuthorNameWhenAuthorExists() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());

        mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "test comment"));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].authorName", is(user.getUsername()));
    }

    @Test
    void shouldHaveCorrectAuthorNameWhenAuthorDoesNotExist() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "test comment"));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].authorName", nullValue());
    }

    @Test
    void shouldReturnUserRatingAsNullWhenAnonymousUserRequests() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "test comment"));
        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), true));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].userRating", nullValue());
    }

    @Test
    void shouldHaveCorrectUserRatingWhenUserIsAuthenticated() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var loggedUser = mongoTemplate.save(UserTestDataFactory.create());

        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), "test comment"));
        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), loggedUser.getId(), true));

        var token = JwtTestFactory.create(loggedUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].userRating", notNullValue());
    }
}

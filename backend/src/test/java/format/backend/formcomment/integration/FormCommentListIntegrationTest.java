package format.backend.formcomment.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormCommentListIntegrationTest extends BaseIntegrationTest {

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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));
        mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));
        mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));

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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val olderComment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));
        val newerComment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));

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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());

        mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].authorName", is(user.getUsername()));
    }

    @Test
    void shouldHaveCorrectAuthorNameWhenAuthorDoesNotExist() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].authorName", nullValue());
    }

    @Test
    void shouldReturnUserRatingAsNullWhenAnonymousUserRequests() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));
        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.UPVOTE));

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].userRating", nullValue());
    }

    @Test
    void shouldHaveCorrectUserRatingWhenUserIsAuthenticated() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val loggedUser = mongoTemplate.save(UserTestDataFactory.create());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId()));
        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), loggedUser.getId(), FormCommentRatingType.UPVOTE));

        val token = JwtTestFactory.create(loggedUser);
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

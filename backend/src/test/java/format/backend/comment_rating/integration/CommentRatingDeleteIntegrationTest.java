package format.backend.comment_rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.comment.datafactory.datafactory.CommentTestDataFactory;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.datafactory.CommentRatingTestDataFactory;
import format.backend.comment_rating.entity.CommentRatingEntity;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

class CommentRatingDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}/rating".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
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
                .pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentDoesNotExist() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentBelongsToAnotherForm() {
        var form1 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var form2 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));

        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form1.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form2.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentNotRatedByUser() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteUpvoteAndDecrementCommentRatingScore() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var rating = mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), true));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(CommentEntity::getId).is(comment.getId())),
                Update.update(CommentEntity::getRatingScore, 1L),
                CommentEntity.class);

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(rating.getId(), CommentRatingEntity.class))
                .isNull();
        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isZero();
    }

    @Test
    void shouldDeleteDownvoteAndIncrementCommentRatingScore() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var rating = mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), false));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(CommentEntity::getId).is(comment.getId())),
                Update.update(CommentEntity::getRatingScore, -1L),
                CommentEntity.class);

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(rating.getId(), CommentRatingEntity.class))
                .isNull();
        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isZero();
    }
}

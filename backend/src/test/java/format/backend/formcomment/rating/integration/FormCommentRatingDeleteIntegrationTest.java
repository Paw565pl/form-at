package format.backend.formcomment.rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

final class FormCommentRatingDeleteIntegrationTest extends BaseIntegrationTest {

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
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val token = JwtTestFactory.create(user);
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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val token = JwtTestFactory.create(user);
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
        val form1 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form1.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
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
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val rating = mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.UPVOTE));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(FormCommentEntity::getId).is(comment.getId())),
                Update.update(FormCommentEntity::getRatingScore, 1L),
                FormCommentEntity.class);

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        val updatedComment = mongoTemplate.findById(comment.getId(), FormCommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isZero();

        assertThat(mongoTemplate.findById(rating.getId(), FormCommentRatingEntity.class))
                .isNull();
    }

    @Test
    void shouldDeleteDownvoteAndIncrementCommentRatingScore() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val rating = mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.DOWNVOTE));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(FormCommentEntity::getId).is(comment.getId())),
                Update.update(FormCommentEntity::getRatingScore, -1L),
                FormCommentEntity.class);

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        val updatedComment = mongoTemplate.findById(comment.getId(), FormCommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isZero();

        assertThat(mongoTemplate.findById(rating.getId(), FormCommentRatingEntity.class))
                .isNull();
    }
}

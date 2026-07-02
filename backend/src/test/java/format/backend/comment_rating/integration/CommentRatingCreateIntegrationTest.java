package format.backend.comment_rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.comment.datafactory.datafactory.CommentTestDataFactory;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.datafactory.CommentRatingTestDataFactory;
import format.backend.comment_rating.dto.CommentRatingRequestDto;
import format.backend.comment_rating.entity.RatingType;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import io.restassured.http.ContentType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

public class CommentRatingCreateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}/rating".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
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
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
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
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentBelongsToAnotherForm() {
        var form1 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var form2 = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));

        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form1.getId(), user.getId(), "Comment content"));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form2.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldAddNewUpvoteAndIncrementCommentRatingScore() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "Comment content"));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("type", is(RatingType.UPVOTE.name()));

        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(1L);
    }

    @Test
    void shouldAddNewDownvoteAndDecrementCommentRatingScore() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "Comment content"));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.DOWNVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(RatingType.DOWNVOTE.name()));

        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(-1L);
    }

    @Test
    void shouldUpdateUpvoteToDownvoteAndApplyMinusTwoDelta() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "Comment content"));

        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), true));
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
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.DOWNVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(RatingType.DOWNVOTE.name()));

        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(-1L);
    }

    @Test
    void shouldUpdateDownvoteToUpvoteAndApplyPlusTwoDelta() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "Comment content"));

        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), false));
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
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(RatingType.UPVOTE.name()));

        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(1L);
    }

    @Test
    void shouldNotModifyCommentRatingScoreWhenSubmittingSameRatingType() {
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId(), "Comment content"));

        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), true));
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
                .contentType(ContentType.JSON)
                .body(new CommentRatingRequestDto(RatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(RatingType.UPVOTE.name()));

        var updatedComment = mongoTemplate.findById(comment.getId(), CommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(1L);
    }
}

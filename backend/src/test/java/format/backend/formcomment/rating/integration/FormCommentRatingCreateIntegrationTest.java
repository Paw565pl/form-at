package format.backend.formcomment.rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.domain.entity.UserEntity;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.formcomment.application.rating.upsert.UpsertFormCommentRatingRequestDto;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import io.restassured.http.ContentType;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;

final class FormCommentRatingCreateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}/rating".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE))
                .when()
                .post(PATH)
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
                .contentType(ContentType.JSON)
                .body(new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE))
                .when()
                .post(PATH)
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
                .contentType(ContentType.JSON)
                .body(new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE))
                .when()
                .post(PATH)
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
                .contentType(ContentType.JSON)
                .body(new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldAddNewUpvoteAndIncrementCommentRatingScore() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("type", is(FormCommentRatingType.UPVOTE.name()));

        verifyDbState(comment, 1, user, request);
    }

    @Test
    void shouldAddNewDownvoteAndDecrementCommentRatingScore() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormCommentRatingRequestDto(FormCommentRatingType.DOWNVOTE);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(FormCommentRatingType.DOWNVOTE.name()));

        verifyDbState(comment, -1, user, request);
    }

    @Test
    void shouldUpdateUpvoteToDownvoteAndApplyMinusTwoDelta() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.UPVOTE));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(FormCommentEntity::getId).is(comment.getId())),
                Update.update(FormCommentEntity::getRatingScore, 1L),
                FormCommentEntity.class);

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormCommentRatingRequestDto(FormCommentRatingType.DOWNVOTE);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(request.type().name()));

        verifyDbState(comment, -1, user, request);
    }

    @Test
    void shouldUpdateDownvoteToUpvoteAndApplyPlusTwoDelta() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.DOWNVOTE));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(FormCommentEntity::getId).is(comment.getId())),
                Update.update(FormCommentEntity::getRatingScore, -1L),
                FormCommentEntity.class);

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(request.type().name()));

        verifyDbState(comment, 1, user, request);
    }

    @Test
    void shouldNotModifyCommentRatingScoreWhenSubmittingSameRatingType() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.UPVOTE));
        mongoTemplate.updateFirst(
                Query.query(Criteria.where(FormCommentEntity::getId).is(comment.getId())),
                Update.update(FormCommentEntity::getRatingScore, 1L),
                FormCommentEntity.class);

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormCommentRatingRequestDto(FormCommentRatingType.UPVOTE);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("type", is(FormCommentRatingType.UPVOTE.name()));

        verifyDbState(comment, 1L, user, request);
    }

    private void verifyDbState(
            FormCommentEntity comment,
            long expectedRatingScore,
            UserEntity user,
            UpsertFormCommentRatingRequestDto request) {
        val updatedComment = mongoTemplate.findById(comment.getId(), FormCommentEntity.class);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getRatingScore()).isEqualTo(expectedRatingScore);

        val commentRatings = mongoTemplate.findAll(FormCommentRatingEntity.class);
        assertThat(commentRatings).hasSize(1);

        val commentRating = commentRatings.getFirst();
        assertThat(commentRating.getCommentId()).isEqualTo(comment.getId());
        assertThat(commentRating.getAuthorId()).isEqualTo(user.getId());
        assertThat(commentRating.getType()).isEqualTo(request.type());
    }
}

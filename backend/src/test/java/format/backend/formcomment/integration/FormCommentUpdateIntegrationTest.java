package format.backend.formcomment.integration;

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
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.formcomment.application.shared.FormCommentRequestDto;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import io.restassured.http.ContentType;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormCommentUpdateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        val formId = ObjectId.get().toHexString();
        val commentId = ObjectId.get().toHexString();

        given().pathParam(FORM_PATH_PARAM, formId)
                .pathParam(COMMENT_PATH_PARAM, commentId)
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto("comment"))
                .when()
                .put(PATH)
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
                .pathParam(FORM_PATH_PARAM, "nonexistent-slug")
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto("comment"))
                .when()
                .put(PATH)
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
                .body(new FormCommentRequestDto("comment"))
                .when()
                .put(PATH)
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
                .body(new FormCommentRequestDto("comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotTheCommentAuthor() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), ownerUser.getId()));

        val attackerToken = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(attackerToken);

        given().auth()
                .oauth2(attackerToken.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto("updated comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNotValid() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto("   "))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldUpdateCommentAndReturnUserRatingIfExists() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), user.getId(), FormCommentRatingType.UPVOTE));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto("updated comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(comment.getId()))
                .body("userRating", notNullValue());
    }

    @Test
    void shouldUpdateCommentSuccessfully() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val newContent = "updated comment";

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto(newContent))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("authorName", is(user.getUsername()))
                .body("content", is(newContent));

        verifyDbState(form, user, newContent);
    }

    @Test
    void shouldResolveFormIdBySlugAndUpdateComment() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), user.getId()));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val newContent = "updated comment";

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new FormCommentRequestDto(newContent))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("authorName", is(user.getUsername()))
                .body("content", is(newContent));

        verifyDbState(form, user, newContent);
    }

    private void verifyDbState(FormEntity form, UserEntity user, String newContent) {
        val savedComments = mongoTemplate.findAll(FormCommentEntity.class);
        assertThat(savedComments).hasSize(1);

        val savedComment = savedComments.getFirst();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getFormId()).isEqualTo(form.getId());
        assertThat(savedComment.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedComment.getContent()).isEqualTo(newContent);
    }
}

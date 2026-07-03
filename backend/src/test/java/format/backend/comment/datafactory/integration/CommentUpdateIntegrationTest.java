package format.backend.comment.datafactory.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.comment.datafactory.datafactory.CommentTestDataFactory;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.datafactory.CommentRatingTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import io.restassured.http.ContentType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CommentUpdateIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        var formId = ObjectId.get().toHexString();
        var commentId = ObjectId.get().toHexString();

        given().pathParam(FORM_PATH_PARAM, formId)
                .pathParam(COMMENT_PATH_PARAM, commentId)
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("comment"))
                .when()
                .put(PATH)
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
                .pathParam(FORM_PATH_PARAM, "nonexistent-slug")
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentDoesNotExist() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnNotFoundWhenCommentBelongsToAnotherForm() {
        var form1 = mongoTemplate.save(FormTestDataFactory.create());
        var form2 = mongoTemplate.save(FormTestDataFactory.create());

        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form1.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form2.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotTheCommentAuthor() {
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());

        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), ownerUser.getId()));

        var attackerToken = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(attackerToken);

        given().auth()
                .oauth2(attackerToken.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("updated comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNotValid() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("   "))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldUpdateCommentSuccessfully() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var newContent = "updated comment";

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto(newContent))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("authorName", is(user.getUsername()))
                .body("content", is(newContent));

        var savedComments = mongoTemplate.findAll(CommentEntity.class);
        assertThat(savedComments.size()).isEqualTo(1);

        var savedComment = savedComments.getFirst();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getFormId()).isEqualTo(form.getId());
        assertThat(savedComment.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedComment.getContent()).isEqualTo(newContent);
    }

    @Test
    void shouldUpdateCommentAndReturnUserRatingIfExists() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), user.getId(), true));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("updated comment"))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(comment.getId()))
                .body("userRating", notNullValue());
    }

    @Test
    void shouldResolveFormIdBySlugAndUpdateComment() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), user.getId()));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var newContent = "updated comment";

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto(newContent))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("authorName", is(user.getUsername()))
                .body("content", is(newContent));

        var savedComments = mongoTemplate.findAll(CommentEntity.class);
        assertThat(savedComments.size()).isEqualTo(1);

        var savedComment = savedComments.getFirst();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getFormId()).isEqualTo(form.getId());
        assertThat(savedComment.getAuthorId()).isEqualTo(user.getId());
        assertThat(savedComment.getContent()).isEqualTo(newContent);
    }
}

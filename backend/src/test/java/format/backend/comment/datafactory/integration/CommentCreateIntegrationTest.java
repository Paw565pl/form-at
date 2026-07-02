package format.backend.comment.datafactory.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import io.restassured.http.ContentType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CommentCreateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/comments".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedIfUserIsAnonymous() {
        var form = mongoTemplate.save(FormTestDataFactory.create());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("This is a great form!"))
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
                .pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("This is a great form!"))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnBadRequestWhenContentIsNotValid() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto("   "))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldCreateCommentAndReturnCreated() {
        var form = mongoTemplate.save(FormTestDataFactory.create());
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var commentContent = "This is a really helpful form, thanks!";

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new CommentRequestDto(commentContent))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("content", is(commentContent))
                .body("authorName", is(user.getUsername()))
                .body("userRating", nullValue());

        var savedCommentsCount = mongoTemplate.findAll(CommentEntity.class).size();
        assertThat(savedCommentsCount).isEqualTo(1);
    }
}

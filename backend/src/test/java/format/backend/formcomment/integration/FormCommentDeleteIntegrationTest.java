package format.backend.formcomment.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.Role;
import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import java.util.List;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

final class FormCommentDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String COMMENT_PATH_PARAM = "commentId";
    private static final String PATH =
            "/api/v1/forms/{%s}/comments/{%s}".formatted(FORM_PATH_PARAM, COMMENT_PATH_PARAM);

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
    void shouldReturnForbiddenWhenUserIsNeitherOwnerNorAdmin() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), owner.getId()));

        val token = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldDeleteCommentWhenUserIsAdmin() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val admin = mongoTemplate.save(UserTestDataFactory.create());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), owner.getId()));

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(comment.getId(), FormCommentEntity.class))
                .isNull();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(FormCommentRatingEntity::getCommentId)
                                .is(comment.getId())),
                        FormCommentRatingEntity.class))
                .isZero();
    }

    @Test
    void shouldDeleteCommentAndRelatedDataWhenUserIsOwner() {
        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(owner.getId())
                .build());

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), owner.getId()));
        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), owner.getId(), FormCommentRatingType.UPVOTE));

        val token = JwtTestFactory.create(owner);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .pathParam(COMMENT_PATH_PARAM, comment.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(comment.getId(), FormCommentEntity.class))
                .isNull();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(FormCommentRatingEntity::getCommentId)
                                .is(comment.getId())),
                        FormCommentRatingEntity.class))
                .isZero();
    }
}

package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.Role;
import format.backend.comment.datafactory.datafactory.CommentTestDataFactory;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment_rating.datafactory.CommentRatingTestDataFactory;
import format.backend.comment_rating.entity.CommentRatingEntity;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import format.backend.form_rating.entity.FormRatingEntity;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.datafactory.SubmissionsStatisticsTestDataFactory;
import format.backend.submission.entity.SubmissionEntity;
import format.backend.submission.entity.SubmissionsStatisticsEntity;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

class FormDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "idOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(PATH_PARAM, ObjectId.get().toHexString())
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
                .pathParam(PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNeitherOwnerNorAdmin() {
        var owner = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));

        var token = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldDeleteFormWhenUserIsAdmin() {
        var owner = mongoTemplate.save(UserTestDataFactory.create());
        var admin = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));

        var token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(form.getId(), FormEntity.class)).isNull();
    }

    @Test
    void shouldDeleteFormAndAllRelatedDataWhenUserIsOwner() {
        var owner = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, owner.getId()));

        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), owner.getId(), 5));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), owner.getId(), List.of()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(form.getId(), Map.of()));

        var comment = mongoTemplate.save(CommentTestDataFactory.create(form.getId(), owner.getId()));
        mongoTemplate.save(CommentRatingTestDataFactory.create(comment.getId(), owner.getId(), true));

        var token = JwtTestFactory.create(owner);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(mongoTemplate.findById(form.getId(), FormEntity.class)).isNull();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(FormRatingEntity::getFormId).is(form.getId())),
                        FormRatingEntity.class))
                .isZero();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(SubmissionEntity::getFormId).is(form.getId())),
                        SubmissionEntity.class))
                .isZero();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(SubmissionsStatisticsEntity::getFormId)
                                .is(form.getId())),
                        SubmissionsStatisticsEntity.class))
                .isZero();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(CommentEntity::getFormId).is(form.getId())), CommentEntity.class))
                .isZero();
        assertThat(mongoTemplate.count(
                        Query.query(Criteria.where(CommentRatingEntity::getCommentId)
                                .is(comment.getId())),
                        CommentRatingEntity.class))
                .isZero();
    }
}

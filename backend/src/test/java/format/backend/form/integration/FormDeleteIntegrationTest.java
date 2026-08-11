package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.Role;
import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormRatingEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.rating.datafactory.FormRatingTestDataFactory;
import format.backend.formcomment.datafactory.FormCommentTestDataFactory;
import format.backend.formcomment.domain.entity.FormCommentEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingEntity;
import format.backend.formcomment.domain.entity.FormCommentRatingType;
import format.backend.formcomment.rating.datafactory.FormCommentRatingTestDataFactory;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import format.backend.submission.domain.entity.SubmissionEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsEntity;
import format.backend.submission.domain.entity.SubmissionsStatisticsTestDataFactory;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.awaitility.Awaitility;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

final class FormDeleteIntegrationTest extends BaseIntegrationTest {

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(owner.getId())
                .build());

        val token = JwtTestFactory.create(otherUser);
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
        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val admin = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(owner.getId())
                .build());

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
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
        val owner = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(owner.getId())
                .build());

        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), owner.getId(), 5));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), owner.getId(), List.of()));
        mongoTemplate.save(SubmissionsStatisticsTestDataFactory.create(form.getId(), Map.of()));

        val comment = mongoTemplate.save(FormCommentTestDataFactory.create(form.getId(), owner.getId()));
        mongoTemplate.save(FormCommentRatingTestDataFactory.create(
                form.getId(), comment.getId(), owner.getId(), FormCommentRatingType.UPVOTE));

        val token = JwtTestFactory.create(owner);
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
        Awaitility.waitAtMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(mongoTemplate.count(
                                Query.query(Criteria.where(SubmissionEntity::getFormId)
                                        .is(form.getId())),
                                SubmissionEntity.class))
                        .isZero());
        Awaitility.waitAtMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(mongoTemplate.count(
                                Query.query(Criteria.where(SubmissionsStatisticsEntity::getFormId)
                                        .is(form.getId())),
                                SubmissionsStatisticsEntity.class))
                        .isZero());
        Awaitility.waitAtMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(mongoTemplate.count(
                                Query.query(Criteria.where(FormCommentEntity::getFormId)
                                        .is(form.getId())),
                                FormCommentEntity.class))
                        .isZero());
        Awaitility.waitAtMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(mongoTemplate.count(
                                Query.query(Criteria.where(FormCommentRatingEntity::getCommentId)
                                        .is(comment.getId())),
                                FormCommentRatingEntity.class))
                        .isZero());
    }
}

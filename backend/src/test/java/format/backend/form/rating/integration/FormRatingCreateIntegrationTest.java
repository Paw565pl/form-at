package format.backend.form.rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.domain.entity.UserEntity;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.application.rating.upsert.UpsertFormRatingRequestDto;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormRatingEntity;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.rating.datafactory.FormRatingTestDataFactory;
import io.restassured.http.ContentType;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

final class FormRatingCreateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/rating".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new UpsertFormRatingRequestDto(5))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnBadRequestWhenRatingValueIsOutOfRange() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new UpsertFormRatingRequestDto(6))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new UpsertFormRatingRequestDto(0))
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldCreateNewRatingAndIncrementFormCounters() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormRatingRequestDto(4);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("value", is(request.value()));

        verifyDbState(form, user, request, 4, 1);
    }

    @Test
    void shouldUpdateExistingRatingAndAdjustFormRatingsSumDelta() {
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .ratingsCount(1L)
                .ratingsSum(3L)
                .build()));
        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 3));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormRatingRequestDto(5);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("value", is(request.value()));

        verifyDbState(form, user, request, 5, 1);
    }

    @Test
    void shouldNotModifyFormWhenUpdatingRatingWithExactSameValue() {
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .ratingsCount(1L)
                .ratingsSum(5L)
                .build()));
        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        val request = new UpsertFormRatingRequestDto(5);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("value", is(request.value()));

        verifyDbState(form, user, request, 5, 1);
    }

    private void verifyDbState(
            FormEntity form,
            UserEntity user,
            UpsertFormRatingRequestDto request,
            long expectedRatingsSum,
            long expectedRatingsCount) {
        val updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isEqualTo(expectedRatingsSum);
        assertThat(updatedForm.getRatingsCount()).isEqualTo(expectedRatingsCount);

        val formRating = mongoTemplate.findOne(
                Query.query(Criteria.where(FormRatingEntity::getFormId).is(form.getId())), FormRatingEntity.class);
        assertThat(formRating).isNotNull();
        assertThat(formRating.getFormId()).isEqualTo(form.getId());
        assertThat(formRating.getAuthorId()).isEqualTo(user.getId());
        assertThat(formRating.getValue()).isEqualTo(request.value());
    }
}

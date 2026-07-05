package format.backend.form_rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.UserEntity;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.entity.FormRatingEntity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;

class FormRatingCreateIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/rating".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        var form = mongoTemplate.save(FormTestDataFactory.create());

        given().pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(5))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnBadRequestWhenRatingValueIsOutOfRange() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(6))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(0))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldCreateNewRatingAndIncrementFormCounters() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var request = new FormRatingRequestDto(4);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("value", is(request.ratingValue()));

        verifyDbState(form, user, request, 4, 1);
    }

    @Test
    void shouldUpdateExistingRatingAndAdjustFormRatingsSumDelta() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create());
        form.setRatingsCount(1L);
        form.setRatingsSum(3L);
        mongoTemplate.save(form);

        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 3));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var request = new FormRatingRequestDto(5);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("value", is(request.ratingValue()));

        verifyDbState(form, user, request, 5, 1);
    }

    @Test
    void shouldNotModifyFormWhenUpdatingRatingWithExactSameValue() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create());
        form.setRatingsCount(1L);
        form.setRatingsSum(5L);
        mongoTemplate.save(form);

        mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 5));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        var request = new FormRatingRequestDto(5);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("value", is(request.ratingValue()));

        verifyDbState(form, user, request, 5, 1);
    }

    private void verifyDbState(
            FormEntity form,
            UserEntity user,
            FormRatingRequestDto request,
            long expectedRatingsSum,
            long expectedRatingsCount) {
        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isEqualTo(expectedRatingsSum);
        assertThat(updatedForm.getRatingsCount()).isEqualTo(expectedRatingsCount);

        var formRating = mongoTemplate.findOne(
                Query.query(Criteria.where(FormRatingEntity::getFormId).is(form.getId())), FormRatingEntity.class);
        assertThat(formRating).isNotNull();
        assertThat(formRating.getFormId()).isEqualTo(form.getId());
        assertThat(formRating.getAuthorId()).isEqualTo(user.getId());
        assertThat(formRating.getValue()).isEqualTo(request.ratingValue());
    }
}

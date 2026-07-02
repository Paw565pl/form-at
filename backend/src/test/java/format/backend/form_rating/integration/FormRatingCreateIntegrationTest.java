package format.backend.form_rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import format.backend.form_rating.dto.FormRatingRequestDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
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

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(4))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("value", is(4));

        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsCount()).isEqualTo(1L);
        assertThat(updatedForm.getRatingsSum()).isEqualTo(4L);
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

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(5))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("value", is(5));

        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isEqualTo(5L);
        assertThat(updatedForm.getRatingsCount()).isEqualTo(1L);
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

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .contentType(ContentType.JSON)
                .body(new FormRatingRequestDto(5))
                .when()
                .post(PATH)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isEqualTo(5L);
        assertThat(updatedForm.getRatingsCount()).isEqualTo(1L);
    }
}

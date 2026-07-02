package format.backend.form_rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormEntity;
import format.backend.form_rating.datafactory.FormRatingTestDataFactory;
import format.backend.form_rating.entity.FormRatingEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FormRatingDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/rating".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedOnDeleteWhenNotAuthenticated() {
        var form = mongoTemplate.save(FormTestDataFactory.create());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentRating() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteRatingAndDecrementFormCounters() {
        var user = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create());
        form.setRatingsCount(1L);
        form.setRatingsSum(4L);
        mongoTemplate.save(form);

        var existingRating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 4));

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var deletedRatingInDb = mongoTemplate.findById(existingRating.getId(), FormRatingEntity.class);
        assertThat(deletedRatingInDb).isNull();

        var updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isZero();
        assertThat(updatedForm.getRatingsCount()).isZero();
    }
}

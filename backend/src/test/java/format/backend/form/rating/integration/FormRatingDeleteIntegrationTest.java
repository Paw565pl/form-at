package format.backend.form.rating.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormRatingEntity;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.form.rating.datafactory.FormRatingTestDataFactory;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormRatingDeleteIntegrationTest extends BaseIntegrationTest {

    private static final String PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/rating".formatted(PATH_PARAM);

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentRating() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val token = JwtTestFactory.create(user);
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
        val user = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .ratingsCount(1L)
                .ratingsSum(4L)
                .build());
        val existingRating = mongoTemplate.save(FormRatingTestDataFactory.create(form.getId(), user.getId(), 4));

        val token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(PATH_PARAM, form.getId())
                .when()
                .delete(PATH)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        val deletedRatingInDb = mongoTemplate.findById(existingRating.getId(), FormRatingEntity.class);
        assertThat(deletedRatingInDb).isNull();

        val updatedForm = mongoTemplate.findById(form.getId(), FormEntity.class);
        assertThat(updatedForm).isNotNull();
        assertThat(updatedForm.getRatingsSum()).isZero();
        assertThat(updatedForm.getRatingsCount()).isZero();
    }
}

package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FormListIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/forms";

    @Test
    void shouldReturnEmptyList() {
        given().when().get(PATH).then().statusCode(HttpStatus.OK.value()).body("page.totalElements", is(0));
    }

    @Test
    void shouldReturnOnlyPublic() {
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC));
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED));

        given().when().get(PATH).then().statusCode(HttpStatus.OK.value()).body("page.totalElements", is(1));
    }

    @Test
    void shouldPaginateResults() {
        mongoTemplate.save(FormTestDataFactory.create());
        mongoTemplate.save(FormTestDataFactory.create());
        mongoTemplate.save(FormTestDataFactory.create());

        given().queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(3))
                .body("page.totalPages", is(2))
                .body("content", hasSize(2));
    }

    @Test
    void shouldFilterByLanguage() {
        var formPl = FormTestDataFactory.create();
        formPl.setLanguage(Language.PL);
        mongoTemplate.save(formPl);

        var formEn = FormTestDataFactory.create();
        formEn.setLanguage(Language.EN);
        mongoTemplate.save(formEn);

        given().queryParam("language", Language.EN)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1));
    }

    @Test
    void shouldFilterByEstimatedDuration() {
        var form10Min = FormTestDataFactory.create();
        form10Min.setEstimatedDuration(Duration.ofMinutes(10));
        mongoTemplate.save(form10Min);

        var form30Min = FormTestDataFactory.create();
        form30Min.setEstimatedDuration(Duration.ofMinutes(30));
        mongoTemplate.save(form30Min);

        var form60Min = FormTestDataFactory.create();
        form60Min.setEstimatedDuration(Duration.ofMinutes(60));
        mongoTemplate.save(form60Min);

        given().queryParam("minEstimatedDuration", Duration.ofMinutes(15).toString())
                .queryParam("maxEstimatedDuration", Duration.ofMinutes(45).toString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1));
    }

    @Test
    void shouldFilterByAllowsGuestSubmissions() {
        var formGuests = FormTestDataFactory.create();
        formGuests.setAllowsGuestSubmissions(true);
        mongoTemplate.save(formGuests);

        var formNoGuests = FormTestDataFactory.create();
        formNoGuests.setAllowsGuestSubmissions(false);
        mongoTemplate.save(formNoGuests);

        given().queryParam("allowsGuestSubmissions", false)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].id", is(formNoGuests.getId()));
    }

    @Test
    void shouldFilterByAuthorId() {
        var user1 = mongoTemplate.save(UserTestDataFactory.create());
        var user2 = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user1.getId()));
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user2.getId()));

        given().queryParam("authorId", user1.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].id", is(form.getId()));
    }

    @Test
    void shouldFilterBySearchQuery() {
        var formMatch = FormTestDataFactory.create();
        formMatch.setName("cat form");
        mongoTemplate.save(formMatch);

        var formNoMatch = FormTestDataFactory.create();
        formNoMatch.setName("dog form");
        mongoTemplate.save(formNoMatch);

        given().queryParam("searchQuery", "cat")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].id", is(formMatch.getId()));
    }

    @Test
    void shouldSortByCreatedAtDescendingByDefault() {
        var olderForm = FormTestDataFactory.create();
        mongoTemplate.save(olderForm);

        var newerForm = FormTestDataFactory.create();
        mongoTemplate.save(newerForm);

        given().when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(newerForm.getId()))
                .body("content[1].id", is(olderForm.getId()));
    }

    @Test
    void shouldSortByTextScoreWhenSearchQueryIsProvided() {
        var lowerScoreForm = FormTestDataFactory.create();
        lowerScoreForm.setName("target form");
        mongoTemplate.save(lowerScoreForm);

        var higherScoreForm = FormTestDataFactory.create();
        higherScoreForm.setName("target target form");
        mongoTemplate.save(higherScoreForm);

        given().queryParam("searchQuery", "target form")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(higherScoreForm.getId()))
                .body("content[1].id", is(lowerScoreForm.getId()));
    }

    @Test
    void shouldSortBySubmissionsCountDescending() {
        var formFew = FormTestDataFactory.create();
        formFew.setSubmissionsCount(5L);
        mongoTemplate.save(formFew);

        var formMany = FormTestDataFactory.create();
        formMany.setSubmissionsCount(500L);
        mongoTemplate.save(formMany);

        given().queryParam("sort", "submissionsCount,desc")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(formMany.getId()))
                .body("content[1].id", is(formFew.getId()));
    }

    @Test
    void shouldSetCorrectAuthorName() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, user.getId()));

        given().when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].authorName", is(user.getUsername()));
    }

    @Test
    void shouldReturnUserNonPublicFormsIfAuthenticatedAsAuthor() {
        var loggedInUser = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());

        mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, otherUser.getId()));
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED, loggedInUser.getId()));
        mongoTemplate.save(FormTestDataFactory.create(FormStatus.CLOSED, otherUser.getId()));

        var token = JwtTestFactory.create(loggedInUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2));
    }
}

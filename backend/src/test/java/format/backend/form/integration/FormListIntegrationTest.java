package format.backend.form.integration;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormLanguage;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import java.time.Duration;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class FormListIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/forms";

    @Test
    void shouldReturnEmptyListWhenNoData() {
        given().when().get(PATH).then().statusCode(HttpStatus.OK.value()).body("page.totalElements", is(0));
    }

    @Test
    void shouldReturnOnlyPublic() {
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .build());

        given().when().get(PATH).then().statusCode(HttpStatus.OK.value()).body("page.totalElements", is(1));
    }

    @Test
    void shouldPaginateResults() {
        mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

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
    void shouldFilterBySearchQuery() {
        val formMatch = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().name("cat form").build());
        mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().name("dog form").build());

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> given().queryParam("searchQuery", "cat")
                        .when()
                        .get(PATH)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("page.totalElements", is(1))
                        .body("content[0].id", is(formMatch.getId())));
    }

    @Test
    void shouldFilterByLanguage() {
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .language(FormLanguage.PL)
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .language(FormLanguage.EN)
                .build());

        given().queryParam("language", FormLanguage.EN)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1));
    }

    @Test
    void shouldFilterByEstimatedDuration() {
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .estimatedDurationSeconds(Duration.ofMinutes(10).toSeconds())
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .estimatedDurationSeconds(Duration.ofMinutes(30).toSeconds())
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .estimatedDurationSeconds(Duration.ofMinutes(60).toSeconds())
                .build());

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
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .allowsGuestSubmissions(true)
                .build());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .allowsGuestSubmissions(false)
                .build());

        given().queryParam("allowsGuestSubmissions", false)
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].id", is(form.getId()));
    }

    @Test
    void shouldFilterByAuthorId() {
        val user1 = mongoTemplate.save(UserTestDataFactory.create());
        val user2 = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().authorId(user1.getId()).build());
        mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().authorId(user2.getId()).build());

        given().queryParam("authorId", user1.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].id", is(form.getId()));
    }

    @Test
    void shouldSortByCreatedAtDescendingByDefault() {
        val olderForm =
                mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val newerForm =
                mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(newerForm.getId()))
                .body("content[1].id", is(olderForm.getId()));
    }

    @Test
    void shouldSortByTextScoreWhenSearchQueryIsProvided() {
        val lowerScoreForm = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().name("target form").build());
        val higherScoreForm = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .name("target target form")
                .build());

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> given().queryParam("searchQuery", "target form")
                        .when()
                        .get(PATH)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("page.totalElements", is(2))
                        .body("content[0].id", is(higherScoreForm.getId()))
                        .body("content[1].id", is(lowerScoreForm.getId())));
    }

    @Test
    void shouldSortByEstimatedDuration() {
        val form1 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .estimatedDurationSeconds(Duration.ofMinutes(10).toSeconds())
                .build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .estimatedDurationSeconds(Duration.ofMinutes(5).toSeconds())
                .build());

        given().queryParam("sort", "estimatedDuration")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(form2.getId()))
                .body("content[1].id", is(form1.getId()));
    }

    @Test
    void shouldSortByQuestionsCount() {
        val form1 = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().questionsCount(4).build());
        val form2 = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().questionsCount(3).build());

        given().queryParam("sort", "questionsCount")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(form2.getId()))
                .body("content[1].id", is(form1.getId()));
    }

    @Test
    void shouldSortBySubmissionsCount() {
        val form1 = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().submissionsCount(4L).build());
        val form2 = mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().submissionsCount(3L).build());

        given().queryParam("sort", "submissionsCount")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(form2.getId()))
                .body("content[1].id", is(form1.getId()));
    }

    @Test
    void shouldSortByCreatedAt() {
        val form1 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().queryParam("sort", "createdAt")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(form1.getId()))
                .body("content[1].id", is(form2.getId()));
    }

    @Test
    void shouldSortByUpdatedAt() {
        val form1 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());
        val form2 = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        given().queryParam("sort", "updatedAt")
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(2))
                .body("content[0].id", is(form1.getId()))
                .body("content[1].id", is(form2.getId()));
    }

    @Test
    void shouldHaveCorrectAuthorName() {
        val user = mongoTemplate.save(UserTestDataFactory.create());
        mongoTemplate.save(
                FormTestDataFactory.createWithDefaults().authorId(user.getId()).build());

        given().when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1))
                .body("content[0].authorName", is(user.getUsername()));
    }

    @Test
    void shouldReturnUserNonPublicFormsIfAuthenticatedAsAuthor() {
        val loggedInUser = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());

        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(otherUser.getId())
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .authorId(loggedInUser.getId())
                .build());
        mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.CLOSED)
                .authorId(otherUser.getId())
                .build());

        val token = JwtTestFactory.create(loggedInUser);
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

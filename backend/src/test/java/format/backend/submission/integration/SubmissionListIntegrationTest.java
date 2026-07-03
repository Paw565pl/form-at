package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.auth.entity.Role;
import format.backend.core.integration.BaseIntegrationTest;
import format.backend.form.datafactory.FormTestDataFactory;
import format.backend.form.entity.FormStatus;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class SubmissionListIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/submissions".formatted(FORM_PATH_PARAM);

    // --- NEGATIVE TESTS ---

    @Test
    void shouldReturnUnauthorizedWhenUserIsAnonymous() {
        given().pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
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
                .pathParam(FORM_PATH_PARAM, ObjectId.get().toHexString())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNeitherOwnerNorAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var otherUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(otherUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldReturnConflictWhenFormDoesNotSaveSubmissions() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        var form = FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId());
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnConflictWhenFormAuthorIdIsNull() {
        var user = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create());

        var token = JwtTestFactory.create(user);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldReturnEmptyPageWhenNoSubmissionsExist() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(0))
                .body("content", hasSize(0));
    }

    @Test
    void shouldPaginateAndReturnSubmissionsWhenUserIsOwner() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .queryParam("page", 0)
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
    void shouldReturnSubmissionsWhenUserIsAdmin() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var admin = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", is(1));
    }

    @Test
    void shouldHaveCorrectAuthorName() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var submitter = mongoTemplate.save(UserTestDataFactory.create());

        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].authorName", is(submitter.getUsername()));
    }

    @Test
    void shouldSortSubmissionsByIdDescendingByDefault() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));

        var olderSubmission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));
        var newerSubmission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getId())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content[0].id", is(newerSubmission.getId()))
                .body("content[1].id", is(olderSubmission.getId()));
    }

    @Test
    void shouldResolveFormSlugAndReturnSubmissions() {
        var ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        var form = mongoTemplate.save(FormTestDataFactory.create(FormStatus.PUBLIC, ownerUser.getId()));
        var submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        var token = JwtTestFactory.create(ownerUser);
        when(jwtDecoder.decode(anyString())).thenReturn(token);

        given().auth()
                .oauth2(token.getTokenValue())
                .pathParam(FORM_PATH_PARAM, form.getSlug())
                .when()
                .get(PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", is(submission.getId()));
    }
}

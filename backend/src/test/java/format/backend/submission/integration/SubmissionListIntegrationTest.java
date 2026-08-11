package format.backend.submission.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import format.backend.auth.Role;
import format.backend.auth.datafactory.JwtTestFactory;
import format.backend.auth.datafactory.UserTestDataFactory;
import format.backend.core.BaseIntegrationTest;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.entity.FormTestDataFactory;
import format.backend.submission.datafactory.SubmissionTestDataFactory;
import java.util.List;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

final class SubmissionListIntegrationTest extends BaseIntegrationTest {

    private static final String FORM_PATH_PARAM = "formIdOrSlug";
    private static final String PATH = "/api/v1/forms/{%s}/submissions".formatted(FORM_PATH_PARAM);

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
        val user = mongoTemplate.save(UserTestDataFactory.create());
        val token = JwtTestFactory.create(user);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val otherUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(otherUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());

        val form = FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build();
        form.setSaveSubmissions(false);
        mongoTemplate.save(form);

        val token = JwtTestFactory.create(ownerUser);
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
        val admin = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults().build());

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val user1 = mongoTemplate.save(UserTestDataFactory.create());
        val user2 = mongoTemplate.save(UserTestDataFactory.create());
        val user3 = mongoTemplate.save(UserTestDataFactory.create());

        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), user1.getId(), List.of()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), user2.getId(), List.of()));
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), user3.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val admin = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        val token = JwtTestFactory.create(admin, List.of(Role.ADMIN));
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val submitter = mongoTemplate.save(UserTestDataFactory.create());

        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), submitter.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());

        val user1 = mongoTemplate.save(UserTestDataFactory.create());
        val user2 = mongoTemplate.save(UserTestDataFactory.create());

        val olderSubmission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), user1.getId(), List.of()));
        val newerSubmission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), user2.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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
        val ownerUser = mongoTemplate.save(UserTestDataFactory.create());
        val form = mongoTemplate.save(FormTestDataFactory.createWithDefaults()
                .status(FormStatus.PUBLIC)
                .authorId(ownerUser.getId())
                .build());
        val submission =
                mongoTemplate.save(SubmissionTestDataFactory.create(form.getId(), ownerUser.getId(), List.of()));

        val token = JwtTestFactory.create(ownerUser);
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

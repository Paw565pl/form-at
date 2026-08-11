package format.backend.formcomment.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.core.validator.ValidObjectId;
import format.backend.formcomment.application.rating.delete.DeleteFormCommentRatingHandler;
import format.backend.formcomment.application.rating.upsert.UpsertFormCommentRatingHandler;
import format.backend.formcomment.application.rating.upsert.UpsertFormCommentRatingRequestDto;
import format.backend.formcomment.application.rating.upsert.UpsertFormCommentRatingResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments/{formCommentId}/rating")
@RequiredArgsConstructor
class FormCommentRatingController {

    private final UpsertFormCommentRatingHandler upsertHandler;
    private final DeleteFormCommentRatingHandler deleteHandler;

    @IsAuthenticated
    @PutMapping
    UpsertFormCommentRatingResponseDto upsert(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @ValidObjectId @PathVariable String formCommentId,
            @Valid @RequestBody UpsertFormCommentRatingRequestDto requestDto) {
        return upsertHandler.handle(userClaims, formIdOrSlug, formCommentId, requestDto);
    }

    @IsAuthenticated
    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    void delete(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @ValidObjectId @PathVariable String formCommentId) {
        deleteHandler.handle(userClaims, formIdOrSlug, formCommentId);
    }
}

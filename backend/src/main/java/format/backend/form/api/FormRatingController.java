package format.backend.form.api;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.form.application.rating.delete.DeleteFormRatingHandler;
import format.backend.form.application.rating.upsert.UpsertFormRatingHandler;
import format.backend.form.application.rating.upsert.UpsertFormRatingRequestDto;
import format.backend.form.application.rating.upsert.UpsertFormRatingResponseDto;
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
@RequestMapping("/api/v1/forms/{formIdOrSlug}/rating")
@RequiredArgsConstructor
class FormRatingController {

    private final UpsertFormRatingHandler upsertHandler;
    private final DeleteFormRatingHandler deleteHandler;

    @IsAuthenticated
    @PutMapping
    UpsertFormRatingResponseDto upsert(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @Valid @RequestBody UpsertFormRatingRequestDto requestDto) {
        return upsertHandler.handle(userClaims, formIdOrSlug, requestDto);
    }

    @IsAuthenticated
    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    void delete(@AuthenticationPrincipal UserClaims userClaims, @PathVariable String formIdOrSlug) {
        deleteHandler.handle(userClaims, formIdOrSlug);
    }
}

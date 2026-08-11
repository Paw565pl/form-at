package format.backend.formcomment.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.core.validator.ValidObjectId;
import format.backend.formcomment.application.create.CreateFormCommentHandler;
import format.backend.formcomment.application.delete.DeleteFormCommentHandler;
import format.backend.formcomment.application.list.ListFormCommentsHandler;
import format.backend.formcomment.application.shared.FormCommentRequestDto;
import format.backend.formcomment.application.shared.FormCommentResponseDto;
import format.backend.formcomment.application.update.UpdateFormCommentHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments")
@RequiredArgsConstructor
class FormCommentController {

    private final ListFormCommentsHandler listHandler;
    private final CreateFormCommentHandler createHandler;
    private final UpdateFormCommentHandler updateHandler;
    private final DeleteFormCommentHandler deleteHandler;

    @GetMapping
    Page<FormCommentResponseDto> list(
            @AuthenticationPrincipal @Nullable UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            Pageable pageable) {
        return listHandler.handle(userClaims, formIdOrSlug, pageable);
    }

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(CREATED)
    FormCommentResponseDto create(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @Valid @RequestBody FormCommentRequestDto requestDto) {
        return createHandler.handle(userClaims, formIdOrSlug, requestDto);
    }

    @IsAuthenticated
    @PutMapping("/{formCommentId}")
    FormCommentResponseDto update(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @ValidObjectId @PathVariable String formCommentId,
            @Valid @RequestBody FormCommentRequestDto requestDto) {
        return updateHandler.handle(userClaims, formIdOrSlug, formCommentId, requestDto);
    }

    @IsAuthenticated
    @DeleteMapping("/{formCommentId}")
    @ResponseStatus(NO_CONTENT)
    void delete(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @ValidObjectId @PathVariable String formCommentId) {
        deleteHandler.handle(userClaims, formIdOrSlug, formCommentId);
    }
}

package format.backend.submission.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.submission.application.create.CreateSubmissionHandler;
import format.backend.submission.application.delete.DeleteSubmissionHandler;
import format.backend.submission.application.list.ListSubmissionsHandler;
import format.backend.submission.application.own.OwnSubmissionHandler;
import format.backend.submission.application.retrieve.RetrieveSubmissionHandler;
import format.backend.submission.application.shared.dto.SubmissionRequestDto;
import format.backend.submission.application.shared.dto.SubmissionResponseDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms/{formIdOrSlug}/submissions")
@RequiredArgsConstructor
class SubmissionController {

    private final ListSubmissionsHandler listHandler;
    private final RetrieveSubmissionHandler retrieveHandler;
    private final OwnSubmissionHandler ownHandler;
    private final CreateSubmissionHandler createHandler;
    private final DeleteSubmissionHandler deleteHandler;

    @IsAuthenticated
    @GetMapping
    Page<SubmissionResponseDto> list(
            @AuthenticationPrincipal UserClaims userClaims, @PathVariable String formIdOrSlug, Pageable pageable) {
        return listHandler.handle(userClaims, formIdOrSlug, pageable);
    }

    @IsAuthenticated
    @GetMapping("/{submissionId}")
    SubmissionResponseDto retrieve(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @PathVariable String submissionId) {
        return retrieveHandler.handle(userClaims, formIdOrSlug, submissionId);
    }

    @IsAuthenticated
    @GetMapping("/me")
    SubmissionResponseDto own(@AuthenticationPrincipal UserClaims userClaims, @PathVariable String formIdOrSlug) {
        return ownHandler.handle(userClaims, formIdOrSlug);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    SubmissionResponseDto create(
            @AuthenticationPrincipal @Nullable UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @RequestBody @Valid SubmissionRequestDto requestDto) {
        return createHandler.handle(userClaims, formIdOrSlug, requestDto);
    }

    @IsAuthenticated
    @DeleteMapping("/{submissionId}")
    @ResponseStatus(NO_CONTENT)
    void delete(
            @AuthenticationPrincipal UserClaims userClaims,
            @PathVariable String formIdOrSlug,
            @PathVariable String submissionId) {
        deleteHandler.handle(userClaims, formIdOrSlug, submissionId);
    }
}

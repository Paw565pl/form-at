package format.backend.submission.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.submission.dto.SubmissionRequestDto;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.service.SubmissionService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
public class SubmissionController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final SubmissionService submissionService;

    @IsAuthenticated
    @GetMapping
    public Page<@NonNull SubmissionResponseDto> findAllByFormIdOrSlug(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug, Pageable pageable) {
        return submissionService.findAllByFormIdOrSlug(
                keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug, pageable);
    }

    @IsAuthenticated
    @GetMapping("/{submissionId}")
    public SubmissionResponseDto findByFormIdOrSlugAndSubmissionId(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug, @PathVariable String submissionId) {
        return submissionService.findByFormIdOrSlugAndSubmissionId(
                keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug, submissionId);
    }

    @IsAuthenticated
    @GetMapping("/me")
    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug) {
        return submissionService.findByFormIdOrSlugAndAuthorId(keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDto createByFormIdOrSlug(
            @AuthenticationPrincipal @Nullable Jwt jwt,
            @PathVariable String formIdOrSlug,
            @Valid @RequestBody SubmissionRequestDto requestDto) {
        val keycloakJwtClaims = Optional.ofNullable(jwt)
                .map(keycloakJwtClaimsExtractor::getClaims)
                .orElse(null);
        return submissionService.createByFormIdOrSlug(keycloakJwtClaims, formIdOrSlug, requestDto);
    }

    @IsAuthenticated
    @DeleteMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByFormIdOrSlugAndSubmissionId(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug, @PathVariable String submissionId) {
        submissionService.delete(keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug, submissionId);
    }
}

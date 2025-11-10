package format.backend.submission.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms/{formIdOrSlug}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final SubmissionService submissionService;

    @IsAuthenticated
    @GetMapping
    public Page<SubmissionResponseDto> findAllByFormIdOrSlug(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug, Pageable pageable) {
        return submissionService.findAllByFormIdOrSlug(
                keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug, pageable);
    }

    @IsAuthenticated
    @GetMapping("/me")
    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug) {
        return submissionService.findByFormIdOrSlugAndAuthorId(keycloakJwtClaimsExtractor.getClaims(jwt), formIdOrSlug);
    }
}

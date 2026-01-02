package format.backend.form_rating.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.dto.FormRatingResponseDto;
import format.backend.form_rating.service.FormRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forms/{formIdOrSlug}/rating")
public class FormRatingController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final FormRatingService formRatingService;

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormRatingResponseDto add(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @Valid @RequestBody FormRatingRequestDto formRatingRequestDto) {
        return formRatingService.add(
                formIdOrSlug, keycloakJwtClaimsExtractor.getClaims(jwt), formRatingRequestDto);
    }

    @IsAuthenticated
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug) {
        formRatingService.delete(formIdOrSlug, keycloakJwtClaimsExtractor.getClaims(jwt));
    }
}

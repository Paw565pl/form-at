package format.backend.comment_rating.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.comment.validator.ValidCommentId;
import format.backend.comment_rating.dto.CommentRatingRequestDto;
import format.backend.comment_rating.dto.CommentRatingResponseDto;
import format.backend.comment_rating.service.CommentRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments/{commentId}/rating")
public class CommentRatingController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final CommentRatingService commentRatingService;

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentRatingResponseDto add(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId,
            @Valid @RequestBody CommentRatingRequestDto commentRatingRequestDto) {
        return commentRatingService.add(
                formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt), commentRatingRequestDto);
    }

    @IsAuthenticated
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId) {
        commentRatingService.delete(formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt));
    }
}

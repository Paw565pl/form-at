package format.backend.commentRating.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.service.CommentService;
import format.backend.comment.validator.ValidCommentId;
import format.backend.commentRating.dto.CommentRatingRequestDto;
import format.backend.commentRating.service.CommentRatingService;
import format.backend.commentRating.validator.ValidCommentRatingId;
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
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments/{commentId}/rating")
public class CommentRatingController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final CommentService commentService;
    private final CommentRatingService commentRatingService;

    @GetMapping
    public int getRating(@PathVariable String formIdOrSlug, @ValidCommentId @PathVariable String commentId) {
        return commentRatingService.getRating(formIdOrSlug, commentId);
    }

    @IsAuthenticated
    @GetMapping
    public boolean haveRated(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId) {
        return commentRatingService.haveRated(formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt));
    }

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto rate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId,
            @Valid @RequestBody CommentRatingRequestDto commentRatingRequestDto) {
        return commentRatingService.rate(formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt), commentRatingRequestDto );
    }

    @IsAuthenticated
    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId,
            @ValidCommentRatingId @PathVariable String ratingId) {
        commentRatingService.delete(formIdOrSlug, commentId, ratingId, keycloakJwtClaimsExtractor.getClaims(jwt));
    }
}

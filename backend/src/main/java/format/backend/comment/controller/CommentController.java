package format.backend.comment.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.service.CommentService;
import format.backend.comment.validator.ValidCommentId;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments")
public class CommentController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final CommentService commentService;

    @GetMapping
    public Page<@NonNull CommentResponseDto> findAll(
            @AuthenticationPrincipal Jwt jwt, @PathVariable String formIdOrSlug, Pageable pageable) {
        val keycloakJwtClaims = Optional.ofNullable(jwt)
                .map(keycloakJwtClaimsExtractor::getClaims)
                .orElse(null);
        return commentService.findAll(formIdOrSlug, keycloakJwtClaims, pageable);
    }

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto create(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.create(formIdOrSlug, keycloakJwtClaimsExtractor.getClaims(jwt), commentRequestDto);
    }

    @IsAuthenticated
    @PutMapping("/{commentId}")
    public CommentResponseDto update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.update(
                formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt), commentRequestDto);
    }

    @IsAuthenticated
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String formIdOrSlug,
            @ValidCommentId @PathVariable String commentId) {
        commentService.delete(formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt));
    }
}

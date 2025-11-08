package format.backend.comment.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.service.CommentService;
import format.backend.form.validator.ValidFormId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/forms/{formIdOrSlug}/comments")
public class CommentController {
    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final CommentService commentService;

    @GetMapping
    public Page<CommentResponseDto> findAll(
            @ValidFormId @PathVariable String formIdOrSlug,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return commentService.findAll(formIdOrSlug, pageable);
    }

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto create(
            @AuthenticationPrincipal Jwt jwt,
            @ValidFormId @PathVariable String formIdOrSlug,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.create(formIdOrSlug, keycloakJwtClaimsExtractor.getClaims(jwt), commentRequestDto);
    }

    @IsAuthenticated
    @PutMapping("/{commentId}")
    public CommentResponseDto update(
            @AuthenticationPrincipal Jwt jwt,
            @ValidFormId @PathVariable String formIdOrSlug,
            @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.update(
                formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt), commentRequestDto);
    }

    @IsAuthenticated
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @ValidFormId @PathVariable String formIdOrSlug,
            @PathVariable String commentId) {
        commentService.delete(formIdOrSlug, commentId, keycloakJwtClaimsExtractor.getClaims(jwt));
    }
}

package format.backend.comment.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.service.CommentService;
import format.backend.form.validator.ValidFormId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/forms/{formId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public Page<CommentResponseDto> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @NotBlank @ValidFormId @PathVariable String formId,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable) {
        return commentService.findAll(formId, pageable);
    }

    @IsAuthenticated
    @PostMapping
    public CommentResponseDto create(
            @AuthenticationPrincipal Jwt jwt,
            @NotBlank @ValidFormId @PathVariable String formId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        String userId = jwt.getClaimAsString("sub");
        return commentService.create(formId, commentRequestDto, userId);
    }

    @IsAuthenticated
    @PutMapping("/{commentId}")
    public CommentResponseDto update(
            @AuthenticationPrincipal Jwt jwt,
            @NotBlank @ValidFormId @PathVariable String formId,
            @NotBlank @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        String userId = jwt.getClaimAsString("sub");
        return commentService.update(formId, commentId, commentRequestDto, userId);
    }

    @IsAuthenticated
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @NotBlank @ValidFormId @PathVariable String formId,
            @NotBlank @PathVariable String commentId) {
        String userId = jwt.getClaimAsString("sub");
        commentService.delete(formId, commentId, userId);
    }
}

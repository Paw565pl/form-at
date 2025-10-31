package format.backend.comment.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

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

    @PostMapping
    public CommentResponseDto create(
            @AuthenticationPrincipal Jwt jwt,
            @NotBlank @ValidFormId @PathVariable String formId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        String userId = jwt.getClaimAsString("sub");

        return commentService.create(formId, commentRequestDto, userId);
    }
}

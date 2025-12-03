package format.backend.commentRating.controller;

import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.comment.service.CommentService;
import format.backend.commentRating.service.CommentRatingService;
import lombok.RequiredArgsConstructor;
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

}

package format.backend.comment_rating.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.comment.repository.CommentRepository;
import format.backend.comment.service.CommentService;
import format.backend.comment_rating.dto.CommentRatingRequestDto;
import format.backend.comment_rating.dto.CommentRatingResponseDto;
import format.backend.comment_rating.entity.RatingType;
import format.backend.comment_rating.exception.CommentNotRatedByUserException;
import format.backend.comment_rating.mapper.CommentRatingMapper;
import format.backend.comment_rating.repository.CommentRatingRepository;
import format.backend.form.service.FormService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentRatingService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final CommentRatingMapper commentRatingMapper;

    private final UserService userService;
    private final FormService formService;
    private final CommentService commentService;

    @Transactional
    public CommentRatingResponseDto add(
            String formIdOrSlug,
            String commentId,
            KeycloakJwtClaims keycloakJwtClaims,
            CommentRatingRequestDto commentRatingRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = commentService.findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val newType = commentRatingRequestDto.type();

        val existingRatingOpt = commentRatingRepository.findByCommentIdAndAuthorId(comment.getId(), user.getId());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val oldType = RatingType.fromValue(existingRating.getType())
                    .orElseThrow(() -> new IllegalStateException("Invalid rating type in database"));

            if (oldType == newType) {
                return commentRatingMapper.toResponseDto(existingRating);
            }

            existingRating.setType(newType.getValue());
            commentRatingRepository.save(existingRating);

            if (oldType == RatingType.UPVOTE && newType == RatingType.DOWNVOTE) {
                commentRepository.decrementRatingScore(comment.getId());
                commentRepository.decrementRatingScore(comment.getId());
            }

            if (oldType == RatingType.DOWNVOTE && newType == RatingType.UPVOTE) {
                commentRepository.incrementRatingScore(comment.getId());
                commentRepository.incrementRatingScore(comment.getId());
            }

            return commentRatingMapper.toResponseDto(existingRating);
        }

        val rating = commentRatingMapper.toEntity(commentRatingRequestDto, comment, user);

        commentRatingRepository.save(rating);

        if (newType == RatingType.UPVOTE) {
            commentRepository.incrementRatingScore(comment.getId());
        } else {
            commentRepository.decrementRatingScore(comment.getId());
        }

        return commentRatingMapper.toResponseDto(rating);
    }

    @Transactional
    public void delete(String formIdOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = commentService.findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val existingRating = commentRatingRepository
                .findByCommentIdAndAuthorId(comment.getId(), user.getId())
                .orElseThrow(() -> new CommentNotRatedByUserException(commentId));

        val oldType = RatingType.fromValue(existingRating.getType())
                .orElseThrow(() -> new IllegalStateException("Invalid rating type in database"));

        commentRatingRepository.delete(existingRating);

        if (oldType == RatingType.UPVOTE) {
            commentRepository.decrementRatingScore(comment.getId());
        } else {
            commentRepository.incrementRatingScore(comment.getId());
        }
    }
}

package format.backend.comment_rating.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.comment.repository.CommentRepository;
import format.backend.comment.service.CommentService;
import format.backend.comment_rating.dto.CommentRatingRequestDto;
import format.backend.comment_rating.dto.CommentRatingResponseDto;
import format.backend.comment_rating.entity.RatingType;
import format.backend.comment_rating.exception.CommentNotRatedByUserException;
import format.backend.comment_rating.mapper.CommentRatingMapper;
import format.backend.comment_rating.repository.CommentRatingRepository;
import format.backend.form.service.FormService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentRatingService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final CommentRatingMapper commentRatingMapper;

    private final FormService formService;
    private final CommentService commentService;

    @Transactional
    public CommentRatingResponseDto add(
            String formIdOrSlug,
            String commentId,
            KeycloakJwtClaims keycloakJwtClaims,
            CommentRatingRequestDto commentRatingRequestDto) {
        val formId = ObjectId.isValid(formIdOrSlug)
                ? formIdOrSlug
                : formService.findOrThrow(formIdOrSlug).getId();
        val comment = commentService.findOrThrow(commentId);

        if (!Objects.equals(comment.getFormId(), formId)) throw new ResponseStatusException(NOT_FOUND);

        val newType = commentRatingRequestDto.type();
        val existingRatingOpt =
                commentRatingRepository.findByCommentIdAndAuthorId(comment.getId(), keycloakJwtClaims.sub());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val existingRatingType = RatingType.fromValue(existingRating.getType())
                    .orElseThrow(() -> new IllegalStateException("Invalid rating type in database"));

            if (existingRatingType == newType) return commentRatingMapper.toResponseDto(existingRating);

            existingRating.setType(newType.getValue());
            commentRatingRepository.save(existingRating);

            if (existingRatingType == RatingType.DOWNVOTE && newType == RatingType.UPVOTE) {
                commentRepository.updateRatingScore(commentId, 2);
            } else if (existingRatingType == RatingType.UPVOTE && newType == RatingType.DOWNVOTE) {
                commentRepository.updateRatingScore(commentId, -2);
            }

            return commentRatingMapper.toResponseDto(existingRating);
        }

        val newRating = commentRatingMapper.toEntity(commentRatingRequestDto, comment.getId(), keycloakJwtClaims.sub());
        val savedNewRating = commentRatingRepository.save(newRating);

        if (newType == RatingType.UPVOTE) {
            commentRepository.updateRatingScore(comment.getId(), 1);
        } else if (newType == RatingType.DOWNVOTE) {
            commentRepository.updateRatingScore(comment.getId(), -1);
        }

        return commentRatingMapper.toResponseDto(savedNewRating);
    }

    @Transactional
    public void delete(String formIdOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val formId = ObjectId.isValid(formIdOrSlug)
                ? formIdOrSlug
                : formService.findOrThrow(formIdOrSlug).getId();
        val comment = commentService.findOrThrow(commentId);

        if (!Objects.equals(comment.getFormId(), formId)) throw new ResponseStatusException(NOT_FOUND);

        val existingRating = commentRatingRepository
                .findByCommentIdAndAuthorId(comment.getId(), keycloakJwtClaims.sub())
                .orElseThrow(() -> new CommentNotRatedByUserException(commentId));
        val existingRatingType = RatingType.fromValue(existingRating.getType())
                .orElseThrow(() -> new IllegalStateException("Invalid rating type in database"));

        commentRatingRepository.delete(existingRating);

        if (existingRatingType == RatingType.UPVOTE) {
            commentRepository.updateRatingScore(comment.getId(), -1);
        } else if (existingRatingType == RatingType.DOWNVOTE) {
            commentRepository.updateRatingScore(comment.getId(), 1);
        }
    }
}

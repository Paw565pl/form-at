package format.backend.commentRating.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.exception.CommentNotFoundException;
import format.backend.comment.repository.CommentRepository;
import format.backend.commentRating.dto.CommentRatingRequestDto;
import format.backend.commentRating.dto.CommentRatingResponseDto;
import format.backend.commentRating.entity.CommentRatingEntity;
import format.backend.commentRating.mapper.CommentRatingMapper;
import format.backend.commentRating.repository.CommentRatingRepository;
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

    private CommentEntity findOrThrow(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Transactional
    public CommentRatingResponseDto add(String formIdOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims, CommentRatingRequestDto commentRatingRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val newType = commentRatingRequestDto.type().getValue();

        val existingRatingOpt = commentRatingRepository.findByCommentIdAndAuthorId(comment.getId(), user.getId());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val oldType = existingRating.getType();

            if (oldType  == newType) {
                return commentRatingMapper.toResponseDto(existingRating);
            }

            val delta = newType - oldType;

            existingRating.setType(newType);
            commentRatingRepository.save(existingRating);
            commentRepository.updateRatingScore(comment.getId(), delta);

            return commentRatingMapper.toResponseDto(existingRating);
        }

        val rating = new CommentRatingEntity(comment, user);
        rating.setType(newType);

        commentRatingRepository.save(rating);
        commentRepository.updateRatingScore(comment.getId(), newType);

        return commentRatingMapper.toResponseDto(rating);
    }

    @Transactional
    public void delete(String formIdOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val existingRating = commentRatingRepository
                .findByCommentIdAndAuthorId(comment.getId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User has not rated this comment"));

        int delta = -existingRating.getType();

        commentRatingRepository.delete(existingRating);
        commentRepository.updateRatingScore(comment.getId(), delta);
    }
}

package format.backend.commentRating.service;

import format.backend.auth.service.UserService;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.exception.CommentNotFoundException;
import format.backend.comment.repository.CommentRepository;
import format.backend.commentRating.repository.CommentRatingRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentRatingService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;

    private final UserService userService;

    private CommentEntity findOrThrow(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }
}

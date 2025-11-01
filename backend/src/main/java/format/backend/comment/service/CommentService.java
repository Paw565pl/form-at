package format.backend.comment.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.repository.UserRepository;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.mapper.CommentMapper;
import format.backend.comment.repository.CommentRepository;
import format.backend.form.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private static final String FORM_NOT_FOUND = "Form not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String COMMENT_NOT_FOUND = "Comment not found";
    private static final String COMMENT_FORM_MISMATCH = "Comment doesn't match given form";
    private static final String NOT_AUTHOR = "You are not the author of this comment";

    private final MongoTemplate mongoTemplate;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final CommentMapper commentMapper;

    public Page<CommentResponseDto> findAll(String formId, Pageable pageable) {
        if (!formRepository.existsById(formId)) {
            throw new ResponseStatusException(NOT_FOUND, FORM_NOT_FOUND);
        }

        Page<CommentEntity> comments = commentRepository.findByFormId(formId, pageable);
        if (comments.isEmpty()) {
            return Page.empty(pageable);
        }

        return comments.map(commentMapper::toResponseDto);
    }

    @Transactional
    public CommentResponseDto create(String formId, CommentRequestDto commentRequestDto, String userId) {
        var form = formRepository
                .findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, FORM_NOT_FOUND));

        var user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND));

        CommentEntity comment = commentMapper.toEntity(commentRequestDto);
        comment.setForm(form);
        comment.setAuthor(user);

        CommentEntity saved = commentRepository.save(comment);

        return commentMapper.toResponseDto(saved);
    }

    @Transactional
    public CommentResponseDto update(
            String formId, String commentId, CommentRequestDto commentRequestDto, String userId) {
        var form = formRepository
                .findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, FORM_NOT_FOUND));

        var user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND));

        var comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND));

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND, COMMENT_FORM_MISMATCH);
        }

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, NOT_AUTHOR);
        }

        comment.setContent(commentRequestDto.getContent());

        CommentEntity updated = commentRepository.save(comment);

        return commentMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(String formId, String commentId, String userId) {
        var form = formRepository
                .findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, FORM_NOT_FOUND));

        var user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND));

        var comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND));

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND, COMMENT_FORM_MISMATCH);
        }

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, NOT_AUTHOR);
        }

        commentRepository.delete(comment);
    }
}

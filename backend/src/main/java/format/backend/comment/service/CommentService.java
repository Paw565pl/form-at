package format.backend.comment.service;

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

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final MongoTemplate mongoTemplate;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final CommentMapper commentMapper;

    public Page<CommentResponseDto> findAll(String formId, Pageable pageable) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Form not found"));

        Page<CommentEntity> comments = commentRepository.findByFormId(formId, pageable);
        if (comments.isEmpty()) {
            return Page.empty(pageable);
        }

        return comments.map(commentMapper::toResponseDto);
    }

    @Transactional
    public CommentResponseDto create(String formId, CommentRequestDto commentRequestDto, String userId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Form not found"));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        CommentEntity comment = commentMapper.toEntity(commentRequestDto);
        comment.setForm(form);
        comment.setAuthor(user);

        CommentEntity saved = commentRepository.save(comment);

        return commentMapper.toResponseDto(saved);
    }

    @Transactional
    public CommentResponseDto update(String formId, String commentId, CommentRequestDto commentRequestDto, String userId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Form not found"));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Comment not found"));

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND, "Comment doesn't match given form");
        }

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "You are not the author of this comment");
        }

        comment.setContent(commentRequestDto.getContent());

        CommentEntity updated = commentRepository.save(comment);

        return commentMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(String formId, String commentId, String userId) {
        var form = formRepository.findById(formId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Form not found"));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Comment not found"));

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND, "Comment doesn't match given form");
        }

        if (comment.getAuthor() == null || !comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "You are not the author of this comment");
        }

        commentRepository.delete(comment);
    }
}

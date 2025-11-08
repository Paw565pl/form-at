package format.backend.comment.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.entity.Role;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.repository.UserRepository;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.exception.CommentNotFoundException;
import format.backend.comment.mapper.CommentMapper;
import format.backend.comment.repository.CommentRepository;
import format.backend.form.entity.FormEntity;
import format.backend.form.exception.FormNotFoundException;
import format.backend.form.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final CommentMapper commentMapper;

    private FormEntity findFormOrThrow(String idOrSlug) {
        val form = ObjectId.isValid(idOrSlug) ? formRepository.findById(idOrSlug) : formRepository.findBySlug(idOrSlug);
        return form.orElseThrow(() -> new FormNotFoundException(idOrSlug));
    }

    private CommentEntity findCommentOrThrow(String id) {
        val comment = commentRepository.findById(id);
        return comment.orElseThrow(() -> new CommentNotFoundException(id));
    }

    private UserEntity findUserOrThrow(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    public Page<CommentResponseDto> findAll(String idOrSlug, Pageable pageable) {
        val form = findFormOrThrow(idOrSlug);

        val comments = commentRepository.findByFormId(form.getId(), pageable);
        if (comments.isEmpty()) {
            return Page.empty(pageable);
        }

        return comments.map(commentMapper::toResponseDto);
    }

    @Transactional
    public CommentResponseDto create(
            String idOrSlug, KeycloakJwtClaims keycloakJwtClaims, CommentRequestDto commentRequestDto) {
        val form = findFormOrThrow(idOrSlug);

        val user = findUserOrThrow(keycloakJwtClaims.sub());

        val comment = commentMapper.toEntity(commentRequestDto);
        comment.setForm(form);
        comment.setAuthor(user);

        val saved = commentRepository.save(comment);

        return commentMapper.toResponseDto(saved);
    }

    @Transactional
    public CommentResponseDto update(
            String idOrSlug,
            String commentId,
            KeycloakJwtClaims keycloakJwtClaims,
            CommentRequestDto commentRequestDto) {

        val form = findFormOrThrow(idOrSlug);
        val comment = findCommentOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        val canUpdate = comment.getAuthor().getId().equals(keycloakJwtClaims.sub())
                || keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!canUpdate) {
            throw new ResponseStatusException(FORBIDDEN);
        }

        comment.setContent(commentRequestDto.content());

        val updated = commentRepository.save(comment);
        return commentMapper.toResponseDto(updated);
    }

    @Transactional
    public void delete(String idOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val form = findFormOrThrow(idOrSlug);

        val comment = findCommentOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) {
            throw new ResponseStatusException(NOT_FOUND);
        }

        val canDelete = comment.getAuthor().getId().equals(keycloakJwtClaims.sub())
                || keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!canDelete) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        commentRepository.delete(comment);
    }
}

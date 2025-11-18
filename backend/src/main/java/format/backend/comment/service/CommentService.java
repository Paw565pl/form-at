package format.backend.comment.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.entity.Role;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.exception.CommentNotFoundException;
import format.backend.comment.mapper.CommentMapper;
import format.backend.comment.repository.CommentRepository;
import format.backend.form.service.FormService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final UserService userService;
    private final FormService formService;

    private CommentEntity findOrThrow(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    public Page<CommentResponseDto> findAll(String idOrSlug, Pageable pageable) {
        val form = formService.findOrThrow(idOrSlug);

        val sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.asc("_id")));
        val comments = commentRepository.findAllByFormId(form.getId(), sortedPageable);

        return comments.map(comment -> {
            val authorName = Optional.ofNullable(comment.getAuthor())
                    .map(UserEntity::getUsername)
                    .orElse(null);
            return commentMapper.toResponseDto(comment, authorName);
        });
    }

    @Transactional
    public CommentResponseDto create(
            String idOrSlug, KeycloakJwtClaims keycloakJwtClaims, CommentRequestDto commentRequestDto) {
        val form = formService.findOrThrow(idOrSlug);
        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val comment = commentMapper.toEntity(commentRequestDto, form, user);

        val saved = commentRepository.save(comment);
        return commentMapper.toResponseDto(saved, keycloakJwtClaims.username());
    }

    @Transactional
    public CommentResponseDto update(
            String idOrSlug,
            String commentId,
            KeycloakJwtClaims keycloakJwtClaims,
            CommentRequestDto commentRequestDto) {
        val form = formService.findOrThrow(idOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val isCommentOwner = Optional.ofNullable(comment.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isCommentOwner || isAdmin)) throw new ResponseStatusException(FORBIDDEN);

        comment.setContent(commentRequestDto.content());

        val updated = commentRepository.save(comment);
        val authorName = Optional.ofNullable(updated.getAuthor())
                .map(UserEntity::getUsername)
                .orElse(null);

        return commentMapper.toResponseDto(updated, authorName);
    }

    @Transactional
    public void delete(String idOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val form = formService.findOrThrow(idOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val isCommentOwner = Optional.ofNullable(comment.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isCommentOwner || isAdmin)) throw new ResponseStatusException(FORBIDDEN);

        commentRepository.delete(comment);
    }
}

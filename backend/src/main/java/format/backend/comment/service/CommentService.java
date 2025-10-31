package format.backend.comment.service;

import format.backend.auth.repository.UserRepository;
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
import org.springframework.web.server.ResponseStatusException;

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
}

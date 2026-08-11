package format.backend.formcomment;

import format.backend.formcomment.domain.exception.FormCommentNotFoundException;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormCommentFacade {

    private final FormCommentRepository formCommentRepository;

    public String resolveIdOrThrow(String id, String formId) {
        val formCommentEntity =
                formCommentRepository.findById(id).orElseThrow(() -> new FormCommentNotFoundException(id));
        if (!Objects.equals(formId, formCommentEntity.getFormId())) throw new FormCommentNotFoundException(id);

        return Objects.requireNonNull(formCommentEntity.getId());
    }

    public long countByAuthorId(String authorId) {
        return formCommentRepository.countAllByAuthorId(authorId);
    }
}

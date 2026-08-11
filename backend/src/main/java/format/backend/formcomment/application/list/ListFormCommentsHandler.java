package format.backend.formcomment.application.list;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.formcomment.application.shared.FormCommentMapper;
import format.backend.formcomment.application.shared.FormCommentResponseDto;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListFormCommentsHandler {

    private final FormFacade formFacade;

    private final FormCommentRepository formCommentRepository;
    private final FormCommentMapper formCommentMapper;

    public Page<FormCommentResponseDto> handle(
            @Nullable UserClaims userClaims, String formIdOrSlug, Pageable pageable) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val userId = userClaims != null ? userClaims.id() : null;

        return formCommentRepository.findAll(userId, formId, pageable).map(formCommentMapper::toResponseDto);
    }
}

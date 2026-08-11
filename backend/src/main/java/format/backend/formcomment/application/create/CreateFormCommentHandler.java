package format.backend.formcomment.application.create;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.formcomment.application.shared.FormCommentMapper;
import format.backend.formcomment.application.shared.FormCommentRequestDto;
import format.backend.formcomment.application.shared.FormCommentResponseDto;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateFormCommentHandler {

    private final FormFacade formFacade;

    private final FormCommentRepository formCommentRepository;
    private final FormCommentMapper formCommentMapper;

    public FormCommentResponseDto handle(UserClaims userClaims, String formIdOrSlug, FormCommentRequestDto requestDto) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val commentEntity = formCommentRepository.save(formCommentMapper.toEntity(requestDto, formId, userClaims.id()));

        return formCommentMapper.toResponseDto(commentEntity, userClaims.username(), null);
    }
}

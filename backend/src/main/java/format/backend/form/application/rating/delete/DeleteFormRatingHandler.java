package format.backend.form.application.rating.delete;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.form.domain.exception.FormNotRatedByUserException;
import format.backend.form.domain.repository.FormRatingRepository;
import format.backend.form.domain.repository.FormRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteFormRatingHandler {

    private final FormFacade formFacade;

    private final FormRepository formRepository;
    private final FormRatingRepository formRatingRepository;

    @Transactional
    public void handle(UserClaims userClaims, String formIdOrSlug) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val formRatingEntity = formRatingRepository
                .findByFormIdAndAuthorId(formId, userClaims.id())
                .orElseThrow(() -> new FormNotRatedByUserException(formIdOrSlug));

        formRatingRepository.delete(formRatingEntity);
        formRepository.updateRatingFields(formId, -1, formRatingEntity.getValue());
    }
}

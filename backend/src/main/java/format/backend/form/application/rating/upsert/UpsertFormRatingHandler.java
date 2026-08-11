package format.backend.form.application.rating.upsert;

import format.backend.auth.UserClaims;
import format.backend.form.FormFacade;
import format.backend.form.domain.repository.FormRatingRepository;
import format.backend.form.domain.repository.FormRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpsertFormRatingHandler {

    private final FormFacade formFacade;

    private final FormRepository formRepository;
    private final FormRatingRepository formRatingRepository;
    private final UpsertFormRatingMapper formRatingMapper;

    @Transactional
    public UpsertFormRatingResponseDto handle(
            UserClaims userClaims, String formIdOrSlug, UpsertFormRatingRequestDto requestDto) {
        val formId = formFacade.resolveIdOrThrow(userClaims, formIdOrSlug);
        val existingFormRatingEntity = formRatingRepository
                .findByFormIdAndAuthorId(formId, userClaims.id())
                .orElse(null);

        if (existingFormRatingEntity != null) {
            val newRatingValue = requestDto.value();
            val existingRatingValue = existingFormRatingEntity.getValue();
            if (Objects.equals(existingRatingValue, newRatingValue)) {
                return formRatingMapper.toResponseDto(existingFormRatingEntity);
            }

            existingFormRatingEntity.setValue(newRatingValue);
            val updatedFormRatingEntity = formRatingRepository.save(existingFormRatingEntity);
            formRepository.updateRatingFields(formId, 0, (long) newRatingValue - existingRatingValue);

            return formRatingMapper.toResponseDto(updatedFormRatingEntity);
        }

        val formRatingEntity =
                formRatingRepository.save(formRatingMapper.toEntity(requestDto, formId, userClaims.id()));
        formRepository.updateRatingFields(formId, 1, formRatingEntity.getValue());

        return formRatingMapper.toResponseDto(formRatingEntity);
    }
}

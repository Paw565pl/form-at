package format.backend.form_rating.service;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.form.repository.FormRepository;
import format.backend.form.service.FormService;
import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.dto.FormRatingResponseDto;
import format.backend.form_rating.exception.FormNotRatedByUserException;
import format.backend.form_rating.mapper.FormRatingMapper;
import format.backend.form_rating.repository.FormRatingRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FormRatingService {

    private final FormRepository formRepository;
    private final FormRatingRepository formRatingRepository;
    private final FormRatingMapper formRatingMapper;

    private final FormService formService;

    @Transactional
    public FormRatingResponseDto add(
            String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims, FormRatingRequestDto formRatingRequestDto) {
        val formId = formService.findOrThrow(formIdOrSlug).getId();

        val newRatingValue = formRatingRequestDto.ratingValue();
        val existingRatingOpt = formRatingRepository.findByFormIdAndAuthorId(formId, keycloakJwtClaims.sub());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val existingRatingValue = existingRating.getValue();

            if (Objects.equals(existingRatingValue, newRatingValue)) {
                return formRatingMapper.toResponseDto(existingRating);
            }

            existingRating.setValue(newRatingValue);
            formRatingRepository.save(existingRating);

            val delta = newRatingValue - existingRatingValue;
            formRepository.updateRatingsSum(formId, delta);

            return formRatingMapper.toResponseDto(existingRating);
        }

        val rating = formRatingMapper.toEntity(formRatingRequestDto, formId, keycloakJwtClaims.sub());
        val savedRating = formRatingRepository.save(rating);

        formRepository.updateRatingsSum(formId, newRatingValue);
        formRepository.incrementRatingsCount(formId);

        return formRatingMapper.toResponseDto(savedRating);
    }

    @Transactional
    public void delete(String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims) {
        val formId = formService.findOrThrow(formIdOrSlug).getId();
        val existingRating = formRatingRepository
                .findByFormIdAndAuthorId(formId, keycloakJwtClaims.sub())
                .orElseThrow(() -> new FormNotRatedByUserException(formIdOrSlug));

        formRatingRepository.delete(existingRating);

        val ratingValue = existingRating.getValue();
        formRepository.updateRatingsSum(formId, -ratingValue);
        formRepository.decrementRatingsCount(formId);
    }
}

package format.backend.form_rating.service;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
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

    private final UserService userService;
    private final FormService formService;

    @Transactional
    public FormRatingResponseDto add(
            String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims, FormRatingRequestDto formRatingRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val newRatingValue = formRatingRequestDto.ratingValue();
        val existingRatingOpt = formRatingRepository.findByFormIdAndAuthorId(form.getId(), user.getId());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val oldRatingValue = existingRating.getValue();

            if (Objects.equals(oldRatingValue, newRatingValue)) {
                return formRatingMapper.toResponseDto(existingRating);
            }

            existingRating.setValue(newRatingValue);
            formRatingRepository.save(existingRating);

            val delta = newRatingValue - oldRatingValue;
            formRepository.updateRatingsSum(form.getId(), delta);

            return formRatingMapper.toResponseDto(existingRating);
        }

        val rating = formRatingMapper.toEntity(formRatingRequestDto, form, user);
        formRatingRepository.save(rating);

        formRepository.updateRatingsSum(form.getId(), newRatingValue);
        formRepository.incrementRatingsCount(form.getId());

        return formRatingMapper.toResponseDto(rating);
    }

    @Transactional
    public void delete(String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims) {
        val form = formService.findOrThrow(formIdOrSlug);
        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val existingRating = formRatingRepository
                .findByFormIdAndAuthorId(form.getId(), user.getId())
                .orElseThrow(() -> new FormNotRatedByUserException(formIdOrSlug));

        formRatingRepository.delete(existingRating);

        val ratingValue = existingRating.getValue();
        formRepository.updateRatingsSum(form.getId(), -ratingValue);

        formRepository.decrementRatingsCount(form.getId());
    }
}

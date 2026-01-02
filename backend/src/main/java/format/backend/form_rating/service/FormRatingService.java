package format.backend.form_rating.service;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.form.repository.FormRepository;
import format.backend.form.service.FormService;
import format.backend.form_rating.dto.FormRatingRequestDto;
import format.backend.form_rating.dto.FormRatingResponseDto;
import format.backend.form_rating.mapper.FormRatingMapper;
import format.backend.form_rating.repository.FormRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
            String formIdOrSlug,
            KeycloakJwtClaims keycloakJwtClaims,
            FormRatingRequestDto formRatingRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val newRating = formRatingRequestDto.rating();
        val existingRatingOpt = formRatingRepository.findByFormIdAndAuthorId(form.getId(), user.getId());

        if (existingRatingOpt.isPresent()) {
            val existingRating = existingRatingOpt.get();
            val oldRating = existingRatingOpt.get().getRating();

            if (Objects.equals(oldRating, newRating)) {
                return formRatingMapper.toResponseDto(existingRating);
            }

            existingRating.setRating(newRating);
            formRatingRepository.save(existingRating);
        }
    }

    @Transactional
    public void delete(String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims) {

    }
}

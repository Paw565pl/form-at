package format.backend.form.application.create;

import com.github.slugify.Slugify;
import format.backend.auth.UserClaims;
import format.backend.core.exception.ValidationException;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.FormResponseDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.application.shared.mapper.FormMapper;
import format.backend.form.application.shared.mapper.QuestionMapper;
import format.backend.form.application.shared.validator.FormImageValidator;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.exception.FormAlreadyExistsException;
import format.backend.form.domain.repository.FormRepository;
import format.backend.upload.UploadFacade;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateFormHandler {

    private final Slugify slugify;
    private final PasswordEncoder passwordEncoder;

    private final UploadFacade uploadFacade;

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final QuestionMapper questionMapper;
    private final FormImageValidator formImageValidator;

    public FormResponseDto handle(UserClaims userClaims, FormRequestDto requestDto) {
        val tempKeys = Stream.concat(
                        Stream.ofNullable(requestDto.thumbnailKey()),
                        requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        val imageValidationErrors = formImageValidator.validate(userClaims, requestDto, tempKeys);
        if (!imageValidationErrors.isEmpty()) throw new ValidationException(imageValidationErrors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = passwordEncoder.encode(requestDto.password());
        val thumbnailKey =
                uploadFacade.resolveDestinationKey(requestDto.thumbnailKey()).orElse(null);
        val questions = requestDto.questions().stream()
                .map(q -> questionMapper.toEntity(
                        q, uploadFacade.resolveDestinationKey(q.imageKey()).orElse(null)))
                .toList();

        final FormEntity formEntity;
        try {
            formEntity = formRepository.save(
                    formMapper.toEntity(requestDto, slug, passwordHash, thumbnailKey, questions, userClaims.id()));
        } catch (DataIntegrityViolationException _) {
            throw new FormAlreadyExistsException(requestDto.name());
        }

        uploadFacade.commit(tempKeys);

        val thumbnail = uploadFacade
                .createPresignedFileUrl(formEntity.getThumbnailKey())
                .orElse(null);
        val questionResponseDtos = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(
                        q, uploadFacade.createPresignedFileUrl(q.getImageKey()).orElse(null)))
                .toList();
        return formMapper.toResponseDto(formEntity, thumbnail, questionResponseDtos, null, userClaims.username());
    }
}

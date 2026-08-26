package format.backend.form.application.update;

import com.github.slugify.Slugify;
import format.backend.auth.UserClaims;
import format.backend.core.exception.ForbiddenException;
import format.backend.core.exception.ValidationException;
import format.backend.form.FormImagesDeletedEvent;
import format.backend.form.FormQuestionsInvalidatedEvent;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.FormResponseDto;
import format.backend.form.application.shared.dto.QuestionRequestDto;
import format.backend.form.application.shared.mapper.FormMapper;
import format.backend.form.application.shared.mapper.QuestionMapper;
import format.backend.form.application.shared.validator.FormImageValidator;
import format.backend.form.domain.entity.FormRatingEntity;
import format.backend.form.domain.entity.QuestionEntity;
import format.backend.form.domain.exception.FormAlreadyExistsException;
import format.backend.form.domain.exception.FormNotFoundException;
import format.backend.form.domain.repository.FormRatingRepository;
import format.backend.form.domain.repository.FormRepository;
import format.backend.upload.UploadFacade;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UpdateFormHandler {

    private final Slugify slugify;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private final UploadFacade uploadFacade;

    private final FormRepository formRepository;
    private final FormRatingRepository formRatingRepository;
    private final FormMapper formMapper;
    private final QuestionMapper questionMapper;
    private final FormImageValidator formImageValidator;

    public UpdateFormHandler(
            Slugify slugify,
            PasswordEncoder passwordEncoder,
            PlatformTransactionManager transactionManager,
            ApplicationEventPublisher eventPublisher,
            UploadFacade uploadFacade,
            FormRepository formRepository,
            FormRatingRepository formRatingRepository,
            FormMapper formMapper,
            QuestionMapper questionMapper,
            FormImageValidator formImageValidator) {
        this.slugify = slugify;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.eventPublisher = eventPublisher;
        this.uploadFacade = uploadFacade;
        this.formRepository = formRepository;
        this.formRatingRepository = formRatingRepository;
        this.formMapper = formMapper;
        this.questionMapper = questionMapper;
        this.formImageValidator = formImageValidator;
    }

    public FormResponseDto handle(UserClaims userClaims, String idOrSlug, FormRequestDto requestDto) {
        val oldFormEntity =
                formRepository.findByIdOrSlug(idOrSlug).orElseThrow(() -> new FormNotFoundException(idOrSlug));
        val isFormOwner = Objects.equals(oldFormEntity.getAuthorId(), userClaims.id());
        if (!isFormOwner) throw new ForbiddenException();

        val oldFormEntityImageKeys = Stream.concat(
                        Stream.ofNullable(oldFormEntity.getThumbnailKey()),
                        oldFormEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        val requestImageKeys = Stream.concat(
                        Stream.ofNullable(requestDto.thumbnailKey()),
                        requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        val newImageKeys = requestImageKeys.stream()
                .filter(key -> !oldFormEntityImageKeys.contains(key))
                .collect(Collectors.toUnmodifiableSet());
        val imageValidationErrors = formImageValidator.validate(userClaims, requestDto, newImageKeys);
        if (!imageValidationErrors.isEmpty()) throw new ValidationException(imageValidationErrors);
        val removedImageKeys = oldFormEntityImageKeys.stream()
                .filter(key -> !requestImageKeys.contains(key))
                .collect(Collectors.toUnmodifiableSet());

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = passwordEncoder.encode(requestDto.password());
        val questions =
                requestDto.questions().stream().map(questionMapper::toEntity).toList();
        val updatedFormEntity = formMapper.updateEntity(oldFormEntity, requestDto, slug, passwordHash);
        val invalidatedQuestionIds = updatedFormEntity.updateQuestions(questions);

        val formEntity = transactionTemplate.execute(_ -> {
            uploadFacade.commit(newImageKeys, userClaims);

            if (!invalidatedQuestionIds.isEmpty()) {
                eventPublisher.publishEvent(new FormQuestionsInvalidatedEvent(
                        Objects.requireNonNull(oldFormEntity.getId()), invalidatedQuestionIds));
            }

            if (!removedImageKeys.isEmpty()) {
                eventPublisher.publishEvent(
                        new FormImagesDeletedEvent(Objects.requireNonNull(oldFormEntity.getId()), removedImageKeys));
            }

            try {
                return formRepository.save(updatedFormEntity);
            } catch (DataIntegrityViolationException _) {
                throw new FormAlreadyExistsException(requestDto.name());
            }
        });

        val thumbnail = uploadFacade.presignGetUrl(formEntity.getThumbnailKey()).orElse(null);
        val questionResponseDtos = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(
                        q, uploadFacade.presignGetUrl(q.getImageKey()).orElse(null)))
                .toList();
        val userRating = formRatingRepository
                .findByFormIdAndAuthorId(Objects.requireNonNull(formEntity.getId()), userClaims.id())
                .map(FormRatingEntity::getValue)
                .orElse(null);
        return formMapper.toResponseDto(formEntity, thumbnail, questionResponseDtos, userRating, userClaims.username());
    }
}

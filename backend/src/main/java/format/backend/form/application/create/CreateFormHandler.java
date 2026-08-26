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
import format.backend.form.domain.exception.FormAlreadyExistsException;
import format.backend.form.domain.repository.FormRepository;
import format.backend.upload.UploadFacade;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CreateFormHandler {

    private final Slugify slugify;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    private final UploadFacade uploadFacade;

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final QuestionMapper questionMapper;
    private final FormImageValidator formImageValidator;

    public CreateFormHandler(
            Slugify slugify,
            PasswordEncoder passwordEncoder,
            PlatformTransactionManager transactionManager,
            UploadFacade uploadFacade,
            FormRepository formRepository,
            FormMapper formMapper,
            QuestionMapper questionMapper,
            FormImageValidator formImageValidator) {
        this.slugify = slugify;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.uploadFacade = uploadFacade;
        this.formRepository = formRepository;
        this.formMapper = formMapper;
        this.questionMapper = questionMapper;
        this.formImageValidator = formImageValidator;
    }

    public FormResponseDto handle(UserClaims userClaims, FormRequestDto requestDto) {
        val requestImageKeys = Stream.concat(
                        Stream.ofNullable(requestDto.thumbnailKey()),
                        requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        val imageValidationErrors = formImageValidator.validate(userClaims, requestDto, requestImageKeys);
        if (!imageValidationErrors.isEmpty()) throw new ValidationException(imageValidationErrors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = passwordEncoder.encode(requestDto.password());
        val questions =
                requestDto.questions().stream().map(questionMapper::toEntity).toList();

        val formEntity = transactionTemplate.execute(_ -> {
            uploadFacade.commit(requestImageKeys, userClaims);

            try {
                return formRepository.save(
                        formMapper.toEntity(requestDto, slug, passwordHash, questions, userClaims.id()));
            } catch (DataIntegrityViolationException _) {
                throw new FormAlreadyExistsException(requestDto.name());
            }
        });

        val thumbnail = uploadFacade.presignGetUrl(formEntity.getThumbnailKey()).orElse(null);
        val questionResponseDtos = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(
                        q, uploadFacade.presignGetUrl(q.getImageKey()).orElse(null)))
                .toList();
        return formMapper.toResponseDto(formEntity, thumbnail, questionResponseDtos, null, userClaims.username());
    }
}

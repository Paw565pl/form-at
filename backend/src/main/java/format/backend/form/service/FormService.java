package format.backend.form.service;

import com.github.slugify.Slugify;
import format.backend.auth.entity.Role;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.core.exception.ValidationException;
import format.backend.form.dto.FormAccessRequestDto;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormFilterDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.exception.FormAlreadyExistsException;
import format.backend.form.exception.FormNotFoundException;
import format.backend.form.mapper.FormMapper;
import format.backend.form.mapper.QuestionMapper;
import format.backend.form.repository.FormRepository;
import format.backend.form.validator.FormValidator;
import format.backend.submission.repository.SubmissionRepository;
import format.backend.upload.service.UploadService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FormService {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final Slugify slugify;

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final FormValidator formValidator;
    private final QuestionMapper questionMapper;
    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final UploadService uploadService;

    private static final Map<String, String> sortFields = Stream.of(
                    "estimatedDuration", "submissionsCount", "createdAt", "updatedAt")
            .collect(Collectors.toUnmodifiableMap(String::toLowerCase, Function.identity()));

    public Page<FormListResponseDto> findAllPublic(FormFilterDto filterDto, Pageable pageable) {
        var query = new Query();
        if (filterDto.searchQuery() != null && !filterDto.searchQuery().isBlank())
            query = TextQuery.queryText(TextCriteria.forDefaultLanguage()
                            .matchingPhrase(filterDto.searchQuery())
                            .caseSensitive(false))
                    .sortByScore();

        query.addCriteria(Criteria.where("status").is(FormStatus.PUBLIC.name()));

        if (filterDto.language() != null)
            query.addCriteria(Criteria.where("language").is(filterDto.language().getMongoValue()));

        if (filterDto.minEstimatedDuration() != null || filterDto.maxEstimatedDuration() != null) {
            val estimatedDurationFilterCriteria = Criteria.where("estimatedDuration");

            if (filterDto.minEstimatedDuration() != null)
                estimatedDurationFilterCriteria.gte(
                        filterDto.minEstimatedDuration().toSeconds());
            if (filterDto.maxEstimatedDuration() != null)
                estimatedDurationFilterCriteria.lte(
                        filterDto.maxEstimatedDuration().toSeconds());

            query.addCriteria(estimatedDurationFilterCriteria);
        }

        if (filterDto.allowsGuestSubmissions() != null)
            query.addCriteria(Criteria.where("allowsGuestSubmissions").is(filterDto.allowsGuestSubmissions()));

        val total = mongoTemplate.count(query, FormEntity.class);
        if (total == 0) return Page.empty(pageable);

        query.with(Sort.by(getSortOrders(pageable)));
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        val forms = mongoTemplate.find(query, FormEntity.class);
        val response = forms.stream()
                .map(f -> {
                    val authorName = Optional.ofNullable(f.getAuthor())
                            .map(UserEntity::getUsername)
                            .orElse(null);
                    return formMapper.toListResponseDto(f, uploadService.getFileUrl(f.getThumbnailKey()), authorName);
                })
                .toList();

        return new PageImpl<>(response, pageable, total);
    }

    private List<Sort.Order> getSortOrders(Pageable pageable) {
        return Stream.concat(
                        pageable.getSort().stream()
                                .filter(o ->
                                        sortFields.containsKey(o.getProperty().toLowerCase()))
                                .map(o -> new Sort.Order(
                                        o.getDirection(),
                                        sortFields.get(o.getProperty().toLowerCase()))),
                        Stream.of(Sort.Order.asc("_id")))
                .toList();
    }

    public FormEntity findOrThrow(String idOrSlug) {
        val form = ObjectId.isValid(idOrSlug) ? formRepository.findById(idOrSlug) : formRepository.findBySlug(idOrSlug);
        return form.orElseThrow(() -> new FormNotFoundException(idOrSlug));
    }

    public FormDetailResponseDto findByIdOrSlug(String idOrSlug) {
        return mapToDetailResponseDto(findOrThrow(idOrSlug));
    }

    public FormDetailResponseDto findPrivateByIdOrSlug(String idOrSlug, @Valid FormAccessRequestDto accessRequestDto) {
        val formEntity = findOrThrow(idOrSlug);

        if (!formEntity.getStatus().equals(FormStatus.PRIVATE)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (!passwordEncoder.matches(accessRequestDto.password(), formEntity.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return mapToDetailResponseDto(formEntity);
    }

    private FormDetailResponseDto mapToDetailResponseDto(FormEntity formEntity) {
        val questions = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(q, uploadService.getFileUrl(q.getImageKey())))
                .toList();
        val authorName = Optional.ofNullable(formEntity.getAuthor())
                .map(UserEntity::getUsername)
                .orElse(null);

        return formMapper.toDetailResponseDto(
                formEntity, uploadService.getFileUrl(formEntity.getThumbnailKey()), authorName, questions);
    }

    @Transactional
    public FormDetailResponseDto create(KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val errors = formValidator.validate(requestDto, keycloakJwtClaims.sub());
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = Optional.ofNullable(requestDto.password())
                .map(passwordEncoder::encode)
                .orElse(null);

        val author = userService.findOrThrow(keycloakJwtClaims.sub());
        val formEntity = formMapper.toEntity(requestDto, slug, passwordHash, author);

        try {
            return mapToDetailResponseDto(formRepository.save(formEntity));
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    @Transactional
    public FormDetailResponseDto update(
            String idOrSlug, KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val oldFormEntity = findOrThrow(idOrSlug);

        val isFormOwner = Optional.ofNullable(oldFormEntity.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val errors = formValidator.validate(requestDto, keycloakJwtClaims.sub());
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = Optional.ofNullable(requestDto.password())
                .map(passwordEncoder::encode)
                .orElse(null);

        val newImageKeys = Stream.concat(
                        Stream.ofNullable(requestDto.thumbnailKey()),
                        requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        val outdatedImageKeys = Stream.concat(
                        Stream.ofNullable(oldFormEntity.getThumbnailKey()),
                        oldFormEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .filter(k -> !newImageKeys.contains(k))
                .toList();

        val updatedFormEntity = formMapper.updateEntityFromDto(requestDto, oldFormEntity, slug, passwordHash);

        try {
            val response = mapToDetailResponseDto(formRepository.save(updatedFormEntity));
            if (!outdatedImageKeys.isEmpty()) uploadService.deleteAllByKeys(outdatedImageKeys);

            return response;
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    public void delete(String idOrSlug, KeycloakJwtClaims keycloakJwtClaims) {
        val formEntity = findOrThrow(idOrSlug);

        val isFormOwner = Optional.ofNullable(formEntity.getAuthor())
                .map(a -> a.getId().equals(keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner || !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val imageKeys = Stream.concat(
                        Stream.ofNullable(formEntity.getThumbnailKey()),
                        formEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .toList();

        formRepository.delete(formEntity);
        if (!imageKeys.isEmpty()) uploadService.deleteAllByKeys(imageKeys);

        submissionRepository.deleteAllByFormId(formEntity.getId());
    }
}

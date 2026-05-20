package format.backend.form.service;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.slugify.Slugify;
import format.backend.auth.entity.Role;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.repository.CommentRepository;
import format.backend.comment_rating.repository.CommentRatingRepository;
import format.backend.core.exception.ValidationException;
import format.backend.form.dto.FormAccessRequestDto;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormFilterDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormListAggregationResult;
import format.backend.form.entity.FormStatus;
import format.backend.form.entity.Language;
import format.backend.form.entity.QuestionEntity;
import format.backend.form.exception.FormAlreadyExistsException;
import format.backend.form.exception.FormNotFoundException;
import format.backend.form.mapper.FormMapper;
import format.backend.form.mapper.QuestionMapper;
import format.backend.form.repository.FormRepository;
import format.backend.form.validator.FormValidator;
import format.backend.form_rating.entity.FormRatingEntity;
import format.backend.form_rating.repository.FormRatingRepository;
import format.backend.submission.repository.SubmissionRepository;
import format.backend.submission.service.SubmissionsStatisticsService;
import format.backend.upload.service.UploadService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.TextCriteria;
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
    private final LanguageDetector languageDetector;

    private final FormRepository formRepository;
    private final FormMapper formMapper;
    private final FormValidator formValidator;
    private final QuestionMapper questionMapper;

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final SubmissionRepository submissionRepository;
    private final FormRatingRepository formRatingRepository;
    private final UserService userService;
    private final UploadService uploadService;
    private final SubmissionsStatisticsService submissionsStatisticsService;

    public static final String AUTHOR_ID_FIELD = "authorId";
    private static final Map<String, String> SORT_FIELDS = Stream.of(
                    "estimatedDuration", "questionsCount", "submissionsCount", "createdAt", "updatedAt")
            .collect(Collectors.toUnmodifiableMap(String::toLowerCase, Function.identity()));

    public Page<@NonNull FormListResponseDto> findAllPublic(
            @Nullable KeycloakJwtClaims keycloakJwtClaims, FormFilterDto filterDto, Pageable pageable) {
        val operations = new ArrayList<AggregationOperation>();

        val isTextQuery =
                filterDto.searchQuery() != null && !filterDto.searchQuery().isBlank();
        if (isTextQuery) {
            val trimmedSearchQuery = filterDto.searchQuery().trim();
            val searchQueryLanguage = languageDetector.detectLanguageOf(trimmedSearchQuery);
            val mongoSearchLanguage =
                    switch (searchQueryLanguage) {
                        case ENGLISH -> Language.EN.getValue();
                        default -> Language.PL.getValue();
                    };

            val textMatch = Aggregation.match(TextCriteria.forLanguage(mongoSearchLanguage)
                    .matchingPhrase(trimmedSearchQuery)
                    .caseSensitive(false));
            operations.add(textMatch);
        }

        val statusCriteria = Criteria.where("status").is(FormStatus.PUBLIC.name());
        val criteria =
                switch (keycloakJwtClaims) {
                    case KeycloakJwtClaims c ->
                        new Criteria()
                                .orOperator(
                                        statusCriteria,
                                        Criteria.where(AUTHOR_ID_FIELD).is(c.sub()));
                    case null -> statusCriteria;
                };
        operations.add(Aggregation.match(criteria));

        if (filterDto.language() != null) {
            val languageMatch = Aggregation.match(
                    Criteria.where("language").is(filterDto.language().getValue()));
            operations.add(languageMatch);
        }

        if (filterDto.minEstimatedDuration() != null || filterDto.maxEstimatedDuration() != null) {
            val estimatedDurationFilterCriteria = Criteria.where("estimatedDurationSeconds");

            if (filterDto.minEstimatedDuration() != null) {
                estimatedDurationFilterCriteria.gte(
                        filterDto.minEstimatedDuration().toSeconds());
            }
            if (filterDto.maxEstimatedDuration() != null) {
                estimatedDurationFilterCriteria.lte(
                        filterDto.maxEstimatedDuration().toSeconds());
            }

            operations.add(Aggregation.match(estimatedDurationFilterCriteria));
        }

        if (filterDto.allowsGuestSubmissions() != null) {
            val allowsGuestSubmissionsMatch =
                    Aggregation.match(Criteria.where("allowsGuestSubmissions").is(filterDto.allowsGuestSubmissions()));
            operations.add(allowsGuestSubmissionsMatch);
        }

        if (filterDto.authorId() != null && !filterDto.authorId().isBlank()) {
            val authorIdMatch =
                    Aggregation.match(Criteria.where(AUTHOR_ID_FIELD).is(filterDto.authorId()));
            operations.add(authorIdMatch);
        }

        val countOperations = Stream.concat(
                        operations.stream(), Stream.of(Aggregation.count().as("count")))
                .toList();
        final long total = Optional.ofNullable(mongoTemplate
                        .aggregate(Aggregation.newAggregation(countOperations), FormEntity.class, Document.class)
                        .getUniqueMappedResult())
                .map(d -> (long) d.getInteger("count"))
                .orElse(0L);
        if (total == 0) return Page.empty(pageable);

        if (isTextQuery) {
            operations.add(Aggregation.addFields()
                    .addField("score")
                    .withValue(new Document("$meta", "textScore"))
                    .build());
        }

        operations.add(Aggregation.sort(Sort.by(getSortOrders(pageable, isTextQuery))));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        operations.add(Aggregation.lookup("users", AUTHOR_ID_FIELD, "_id", "author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        val forms = mongoTemplate
                .aggregate(Aggregation.newAggregation(operations), FormEntity.class, FormListAggregationResult.class)
                .getMappedResults();
        val content = forms.stream()
                .map(f -> formMapper.toListResponseDto(f, uploadService.getFileUrl(f.thumbnailKey())))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Sort.Order> getSortOrders(Pageable pageable, boolean isTextQuery) {
        val textScoreSortOrder = isTextQuery ? Stream.of(Sort.Order.desc("score")) : Stream.<Sort.Order>empty();
        val pageableSortOrders = pageable.getSort().stream()
                .filter(o -> SORT_FIELDS.containsKey(o.getProperty().toLowerCase()))
                .map(o -> new Sort.Order(
                        o.getDirection(), SORT_FIELDS.get(o.getProperty().toLowerCase())));
        val tieBreaker = Stream.of(Sort.Order.asc("_id"));

        return Stream.of(textScoreSortOrder, pageableSortOrders, tieBreaker)
                .flatMap(Function.identity())
                .toList();
    }

    public FormEntity findOrThrow(String idOrSlug) {
        val form = ObjectId.isValid(idOrSlug) ? formRepository.findById(idOrSlug) : formRepository.findBySlug(idOrSlug);
        return form.orElseThrow(() -> new FormNotFoundException(idOrSlug));
    }

    private @Nullable Integer findUserRating(@Nullable String userId, String formId) {
        if (userId == null) return null;
        return formRatingRepository
                .findByFormIdAndAuthorId(formId, userId)
                .map(FormRatingEntity::getValue)
                .orElse(null);
    }

    public FormDetailResponseDto findByIdOrSlug(@Nullable KeycloakJwtClaims keycloakJwtClaims, String idOrSlug) {
        val form = findOrThrow(idOrSlug);
        val permitsAnonymousAccess =
                form.getStatus().equals(FormStatus.PUBLIC) || form.getStatus().equals(FormStatus.UNPUBLIC);

        val userId = Optional.ofNullable(keycloakJwtClaims)
                .map(KeycloakJwtClaims::sub)
                .orElse(null);

        if (!permitsAnonymousAccess) {
            if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            val isAuthor = Objects.equals(form.getAuthorId(), userId);
            if (!isAuthor) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        val userRating = findUserRating(userId, form.getId());
        return mapToDetailResponseDto(form, userRating);
    }

    public FormDetailResponseDto findPrivateByIdOrSlug(
            @Nullable KeycloakJwtClaims keycloakJwtClaims, String idOrSlug, FormAccessRequestDto accessRequestDto) {
        val formEntity = findOrThrow(idOrSlug);
        val userId = Optional.ofNullable(keycloakJwtClaims)
                .map(KeycloakJwtClaims::sub)
                .orElse(null);

        val userRating = findUserRating(userId, formEntity.getId());

        if (!formEntity.getStatus().equals(FormStatus.PRIVATE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(accessRequestDto.password(), formEntity.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return mapToDetailResponseDto(formEntity, userRating);
    }

    private FormDetailResponseDto mapToDetailResponseDto(FormEntity formEntity, @Nullable Integer userRating) {
        val questions = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(q, uploadService.getFileUrl(q.getImageKey())))
                .toList();
        val authorName = Optional.ofNullable(formEntity.getAuthorId())
                .map(authorId -> userService.findOrThrow(authorId).getUsername())
                .orElse(null);

        return formMapper.toDetailResponseDto(
                formEntity, uploadService.getFileUrl(formEntity.getThumbnailKey()), questions, authorName, userRating);
    }

    @Transactional
    public FormDetailResponseDto create(KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val errors = formValidator.validate(requestDto);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = Optional.ofNullable(requestDto.password())
                .map(passwordEncoder::encode)
                .orElse(null);
        val formEntity = formMapper.toEntity(requestDto, slug, passwordHash, keycloakJwtClaims.sub());

        try {
            val savedFormEntity = formRepository.save(formEntity);
            if (savedFormEntity.getSaveSubmissions()) submissionsStatisticsService.create(savedFormEntity);

            val imageKeys = Stream.concat(
                            Stream.ofNullable(requestDto.thumbnailKey()),
                            requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.commitUploads(imageKeys);

            return mapToDetailResponseDto(savedFormEntity, null);
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    @Transactional
    public FormDetailResponseDto update(
            String idOrSlug, KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val formEntity = findOrThrow(idOrSlug);

        val isFormOwner = Objects.equals(formEntity.getAuthorId(), keycloakJwtClaims.sub());
        if (!isFormOwner) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val errors = formValidator.validate(requestDto);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val oldImageKeys = Stream.concat(
                        Stream.ofNullable(formEntity.getThumbnailKey()),
                        formEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = Optional.ofNullable(requestDto.password())
                .map(passwordEncoder::encode)
                .orElse(null);

        val updatedFormEntity = formMapper.updateEntityFromDto(requestDto, formEntity, slug, passwordHash);
        updatedFormEntity.setSubmissionsCount(0L);

        try {
            val savedFormEntity = formRepository.save(updatedFormEntity);
            submissionRepository.deleteAllByFormId(savedFormEntity.getId());
            if (savedFormEntity.getSaveSubmissions()) submissionsStatisticsService.reset(savedFormEntity);
            else submissionsStatisticsService.delete(savedFormEntity.getId());

            val requestImageKeys = Stream.concat(
                            Stream.ofNullable(requestDto.thumbnailKey()),
                            requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());

            val newImageKeys = requestImageKeys.stream()
                    .filter(k -> !oldImageKeys.contains(k))
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.commitUploads(newImageKeys);

            val outdatedImageKeys = oldImageKeys.stream()
                    .filter(k -> !requestImageKeys.contains(k))
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.deleteAllByKeys(outdatedImageKeys);

            val userRating = findUserRating(keycloakJwtClaims.sub(), updatedFormEntity.getId());
            return mapToDetailResponseDto(savedFormEntity, userRating);
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    @Transactional
    public void delete(String idOrSlug, KeycloakJwtClaims keycloakJwtClaims) {
        val formEntity = findOrThrow(idOrSlug);

        val isFormOwner = Objects.equals(formEntity.getAuthorId(), keycloakJwtClaims.sub());
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!isFormOwner && !isAdmin) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val imageKeys = Stream.concat(
                        Stream.ofNullable(formEntity.getThumbnailKey()),
                        formEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        formRepository.delete(formEntity);
        uploadService.deleteAllByKeys(imageKeys);

        val comments = commentRepository.deleteAllByFormId(formEntity.getId());
        val commentIds = comments.stream().map(CommentEntity::getId).toList();
        commentRatingRepository.deleteAllByCommentIdIn(commentIds);

        submissionRepository.deleteAllByFormId(formEntity.getId());
        submissionsStatisticsService.delete(formEntity.getId());
        formRatingRepository.deleteAllByFormId(formEntity.getId());
    }

    @Transactional
    public void incrementSubmissionsCountById(String id) {
        formRepository.incrementSubmissionsCount(id);
    }

    @Transactional
    public void decrementSubmissionsCountById(String id) {
        formRepository.decrementSubmissionsCount(id);
    }
}

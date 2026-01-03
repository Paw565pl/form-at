package format.backend.form.service;

import com.github.slugify.Slugify;
import format.backend.auth.entity.Role;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
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
import format.backend.form.entity.QuestionEntity;
import format.backend.form.exception.FormAlreadyExistsException;
import format.backend.form.exception.FormNotFoundException;
import format.backend.form.mapper.FormMapper;
import format.backend.form.mapper.QuestionMapper;
import format.backend.form.repository.FormRepository;
import format.backend.form.validator.FormValidator;
import format.backend.form_rating.repository.FormRatingRepository;
import format.backend.submission.repository.SubmissionRepository;
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
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
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

    private static final Map<String, String> sortFields = Stream.of(
                    "estimatedDuration", "questionsCount", "submissionsCount", "createdAt", "updatedAt")
            .collect(Collectors.toUnmodifiableMap(String::toLowerCase, Function.identity()));

    public Page<@NonNull FormListResponseDto> findAllPublic(FormFilterDto filterDto, Pageable pageable) {
        val operations = new ArrayList<AggregationOperation>();

        val isTextQuery =
                filterDto.searchQuery() != null && !filterDto.searchQuery().isBlank();
        if (isTextQuery) {
            val textMatch = Aggregation.match(TextCriteria.forDefaultLanguage()
                    .matchingPhrase(filterDto.searchQuery())
                    .caseSensitive(false));
            operations.add(textMatch);
        }

        operations.add(Aggregation.match(Criteria.where("status").is(FormStatus.PUBLIC.name())));

        if (filterDto.language() != null) {
            val languageMatch = Aggregation.match(
                    Criteria.where("language").is(filterDto.language().getMongoValue()));
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
            val authorIdMatch = Aggregation.match(Criteria.where("authorId").is(filterDto.authorId()));
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

        operations.add(Aggregation.lookup("users", "authorId", "_id", "author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());
        operations.add(Aggregation.addFields()
                .addField("questionsCount")
                .withValue(ArrayOperators.arrayOf("questions").length())
                .build());

        operations.add(Aggregation.addFields()
                .addField("ratingAvg")
                .withValue(ConditionalOperators.when(
                                ComparisonOperators.Eq.valueOf("ratingsCount").equalToValue(0))
                        .then(0.0)
                        .otherwise(
                                ArithmeticOperators.Divide.valueOf("ratingsSum").divideBy("ratingsCount")))
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
                .filter(o -> sortFields.containsKey(o.getProperty().toLowerCase()))
                .map(o -> new Sort.Order(
                        o.getDirection(), sortFields.get(o.getProperty().toLowerCase())));
        val tieBreaker = Stream.of(Sort.Order.asc("_id"));

        return Stream.of(textScoreSortOrder, pageableSortOrders, tieBreaker)
                .flatMap(Function.identity())
                .toList();
    }

    public FormEntity findOrThrow(String idOrSlug) {
        val form = ObjectId.isValid(idOrSlug) ? formRepository.findById(idOrSlug) : formRepository.findBySlug(idOrSlug);
        return form.orElseThrow(() -> new FormNotFoundException(idOrSlug));
    }

    public FormDetailResponseDto findByIdOrSlug(@Nullable KeycloakJwtClaims keycloakJwtClaims, String idOrSlug) {
        val form = findOrThrow(idOrSlug);
        val permitsAnonymousAccess =
                form.getStatus().equals(FormStatus.PUBLIC) || form.getStatus().equals(FormStatus.UNPUBLIC);

        if (!permitsAnonymousAccess) {
            val userId = Optional.ofNullable(keycloakJwtClaims)
                    .map(KeycloakJwtClaims::sub)
                    .orElse(null);
            if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            val isAuthor = Optional.ofNullable(form.getAuthor())
                    .map(a -> Objects.equals(a.getId(), userId))
                    .orElse(false);
            if (!isAuthor) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return mapToDetailResponseDto(form);
    }

    public FormDetailResponseDto findPrivateByIdOrSlug(String idOrSlug, FormAccessRequestDto accessRequestDto) {
        val formEntity = findOrThrow(idOrSlug);

        if (!formEntity.getStatus().equals(FormStatus.PRIVATE)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
            val response = mapToDetailResponseDto(formRepository.save(formEntity));

            val imageKeys = Stream.concat(
                            Stream.ofNullable(requestDto.thumbnailKey()),
                            requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.commitUploads(imageKeys);

            return response;
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    @Transactional
    public FormDetailResponseDto update(
            String idOrSlug, KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val oldFormEntity = findOrThrow(idOrSlug);

        val isFormOwner = Optional.ofNullable(oldFormEntity.getAuthor())
                .map(a -> Objects.equals(a.getId(), keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isFormOwner || isAdmin)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val errors = formValidator.validate(requestDto, keycloakJwtClaims.sub());
        if (!errors.isEmpty()) throw new ValidationException(errors);

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = Optional.ofNullable(requestDto.password())
                .map(passwordEncoder::encode)
                .orElse(null);

        val updatedFormEntity = formMapper.updateEntityFromDto(requestDto, oldFormEntity, slug, passwordHash);

        try {
            val response = mapToDetailResponseDto(formRepository.save(updatedFormEntity));

            val newImageKeys = Stream.concat(
                            Stream.ofNullable(requestDto.thumbnailKey()),
                            requestDto.questions().stream().map(QuestionRequestDto::imageKey))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.commitUploads(newImageKeys);

            val outdatedImageKeys = Stream.concat(
                            Stream.ofNullable(oldFormEntity.getThumbnailKey()),
                            oldFormEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                    .filter(Objects::nonNull)
                    .filter(k -> !newImageKeys.contains(k))
                    .collect(Collectors.toUnmodifiableSet());
            uploadService.deleteAllByKeys(outdatedImageKeys);

            return response;
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }

    @Transactional
    public void delete(String idOrSlug, KeycloakJwtClaims keycloakJwtClaims) {
        val formEntity = findOrThrow(idOrSlug);

        val isFormOwner = Optional.ofNullable(formEntity.getAuthor())
                .map(a -> Objects.equals(a.getId(), keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isFormOwner || isAdmin)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val imageKeys = Stream.concat(
                        Stream.ofNullable(formEntity.getThumbnailKey()),
                        formEntity.getQuestions().stream().map(QuestionEntity::getImageKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());

        val comments = commentRepository.findAllByFormId(formEntity.getId());

        formRepository.delete(formEntity);
        uploadService.deleteAllByKeys(imageKeys);

        commentRepository.deleteAllByFormId(formEntity.getId());
        commentRatingRepository.deleteAllByCommentIn(comments);
        submissionRepository.deleteAllByFormId(formEntity.getId());
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

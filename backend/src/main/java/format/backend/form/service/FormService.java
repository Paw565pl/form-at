package format.backend.form.service;

import com.github.slugify.Slugify;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.repository.UserRepository;
import format.backend.form.dto.AnswerRequestDto;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormFilterDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.dto.QuestionRequestDto;
import format.backend.form.entity.FormEntity;
import format.backend.form.entity.FormStatus;
import format.backend.form.exception.FormAlreadyExistsException;
import format.backend.form.exception.FormNotFoundException;
import format.backend.form.exception.MultipleChoiceQuestionAnswersValidationException;
import format.backend.form.exception.RequiredQuestionCountValidationException;
import format.backend.form.exception.SingleChoiceQuestionAnswersValidationException;
import format.backend.form.mapper.FormMapper;
import format.backend.form.mapper.QuestionMapper;
import format.backend.form.repository.FormRepository;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FormService {

    private final MongoTemplate mongoTemplate;
    private final Slugify slugify;

    private final FormRepository formRepository;
    private final UserRepository userRepository;
    private final FormMapper formMapper;
    private final QuestionMapper questionMapper;

    private static final Map<String, String> validSortFields = Stream.of(
                    "name", "estimatedDuration", "submissionsCount", "createdAt", "updatedAt")
            .collect(Collectors.toMap(String::toLowerCase, Function.identity()));

    public Page<FormListResponseDto> findAllPublic(FormFilterDto filterDto, Pageable pageable) {
        var query = new Query();
        query.addCriteria(Criteria.where("status").is(FormStatus.PUBLIC.name()));

        if (filterDto.searchQuery() != null && !filterDto.searchQuery().isBlank())
            query = TextQuery.queryText(TextCriteria.forDefaultLanguage()
                            .matchingPhrase(filterDto.searchQuery())
                            .caseSensitive(false))
                    .sortByScore();
        if (filterDto.language() != null)
            query.addCriteria(Criteria.where("language").is(filterDto.language().getMongoValue()));
        if (filterDto.minEstimatedDuration() != null)
            query.addCriteria(Criteria.where("estimatedDuration").gte(filterDto.minEstimatedDuration()));
        if (filterDto.maxEstimatedDuration() != null)
            query.addCriteria(Criteria.where("estimatedDuration").lte(filterDto.maxEstimatedDuration()));

        var total = mongoTemplate.count(query, FormEntity.class);
        if (total == 0) return Page.empty(pageable);

        query.with(Sort.by(getSortOrders(pageable)));
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        val forms = mongoTemplate.find(query, FormEntity.class);
        val response = forms.stream()
                .map(f -> formMapper.toListResponseDto(f, "TODO: generate image urls"))
                .toList();

        return new PageImpl<>(response, pageable, total);
    }

    private List<Sort.Order> getSortOrders(Pageable pageable) {
        return Stream.concat(
                        pageable.getSort().stream()
                                .filter(o -> validSortFields.containsKey(
                                        o.getProperty().toLowerCase()))
                                .map(o -> new Sort.Order(
                                        o.getDirection(),
                                        validSortFields.get(o.getProperty().toLowerCase()))),
                        Stream.of(Sort.Order.asc("_id")))
                .toList();
    }

    private FormEntity findOrThrow(String idOrSlug) {
        val form = ObjectId.isValid(idOrSlug) ? formRepository.findById(idOrSlug) : formRepository.findBySlug(idOrSlug);
        return form.orElseThrow(() -> new FormNotFoundException(idOrSlug));
    }

    public FormDetailResponseDto findByIdOrSlug(String idOrSlug) {
        return mapToDetailResponseDto(findOrThrow(idOrSlug));
    }

    private FormDetailResponseDto mapToDetailResponseDto(FormEntity formEntity) {
        val questions = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(q, "TODO: generate image urls"))
                .toList();

        return formMapper.toDetailResponseDto(formEntity, "TODO: generate image urls", questions);
    }

    private FormEntity mapToEntity(FormRequestDto requestDto, UserEntity author) {
        val requiredQuestionsCount = requestDto.questions().stream()
                .filter(QuestionRequestDto::isRequired)
                .count();
        if (requiredQuestionsCount < 1) throw new RequiredQuestionCountValidationException();

        val questions = requestDto.questions().stream()
                .map(q -> {
                    switch (q.type()) {
                        case OPEN -> q.answers().clear();
                        case SINGLE_CHOICE -> {
                            val validAnswersCount = q.answers().stream()
                                    .filter(AnswerRequestDto::isCorrect)
                                    .count();
                            if (validAnswersCount != 1) throw new SingleChoiceQuestionAnswersValidationException();
                        }
                        case MULTIPLE_CHOICE -> {
                            val validAnswersCount = q.answers().stream()
                                    .filter(AnswerRequestDto::isCorrect)
                                    .count();
                            if (1 < validAnswersCount
                                    && validAnswersCount < q.answers().size())
                                throw new MultipleChoiceQuestionAnswersValidationException();
                        }
                    }

                    return questionMapper.toEntity(q);
                })
                .toList();

        val slug = slugify.slugify(requestDto.name());
        val passwordHash = "TODO";

        val formEntity = formMapper.toEntity(requestDto, slug, passwordHash, author);
        formEntity.setQuestions(questions);

        return formEntity;
    }

    @Transactional
    public FormDetailResponseDto create(KeycloakJwtClaims keycloakJwtClaims, FormRequestDto requestDto) {
        val author = userRepository
                .findById(keycloakJwtClaims.sub())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        val formEntity = mapToEntity(requestDto, author);

        try {
            return mapToDetailResponseDto(formRepository.save(formEntity));
        } catch (DataIntegrityViolationException e) {
            throw new FormAlreadyExistsException(requestDto.name());
        }
    }
}

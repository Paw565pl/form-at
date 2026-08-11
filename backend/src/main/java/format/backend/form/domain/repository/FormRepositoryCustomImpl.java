package format.backend.form.domain.repository;

import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;
import org.jspecify.annotations.Nullable;
import org.springframework.data.core.TypedPropertyPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

@RequiredArgsConstructor
class FormRepositoryCustomImpl implements FormRepositoryCustom {

    private static final String QUERY = "query";
    private static final String EQUALS = "equals";
    private static final String VALUE = "value";
    private static final String MULTI = "multi";

    private static final String DESCRIPTION = "description";
    private static final String AUTHOR_ID = "authorId";
    private static final String SEARCH_SCORE = "searchScore";

    private static final List<Object> NAME_TEXT_SEARCH_PATH =
            List.of("name", Map.of(VALUE, "name", MULTI, "en"), Map.of(VALUE, "name", MULTI, "pl"));
    private static final List<Object> DESCRIPTION_TEXT_SEARCH_PATH =
            List.of(DESCRIPTION, Map.of(VALUE, DESCRIPTION, MULTI, "en"), Map.of(VALUE, DESCRIPTION, MULTI, "pl"));
    private static final Map<String, TypedPropertyPath<FormEntity, ?>> SORT_FIELDS = Map.ofEntries(
            Map.entry("estimatedDuration".toLowerCase(Locale.ROOT), FormEntity::getEstimatedDurationSeconds),
            Map.entry("questionsCount".toLowerCase(Locale.ROOT), FormEntity::getQuestionsCount),
            Map.entry("submissionsCount".toLowerCase(Locale.ROOT), FormEntity::getSubmissionsCount),
            Map.entry("createdAt".toLowerCase(Locale.ROOT), FormEntity::getCreatedAt),
            Map.entry("updatedAt".toLowerCase(Locale.ROOT), FormEntity::getUpdatedAt));

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<FormListProjection> findAll(@Nullable String userId, FormListCriteria criteria, Pageable pageable) {
        val operations = new ArrayList<AggregationOperation>();

        val isTextQuery =
                criteria.searchQuery() != null && !criteria.searchQuery().isBlank();
        if (isTextQuery) operations.add(createTextSearchStage(userId, criteria));
        else operations.addAll(createMatchOperations(userId, criteria));

        val countOperations = Stream.concat(
                        operations.stream(), Stream.of(Aggregation.count().as("total")))
                .toList();
        val countResult = mongoTemplate
                .aggregate(Aggregation.newAggregation(countOperations), FormEntity.class, CountResult.class)
                .getUniqueMappedResult();
        if (countResult == null || countResult.total() == 0) return Page.empty(pageable);

        if (isTextQuery) {
            operations.add(Aggregation.addFields()
                    .addField(SEARCH_SCORE)
                    .withValue(new Document("$meta", SEARCH_SCORE))
                    .build());
        }

        operations.add(Aggregation.sort(createValidSort(pageable.getSort(), isTextQuery)));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        operations.add(Aggregation.lookup()
                .from("users")
                .localField(AUTHOR_ID)
                .foreignField("_id")
                .as("author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        val forms = mongoTemplate
                .aggregate(Aggregation.newAggregation(operations), FormEntity.class, FormListProjection.class)
                .getMappedResults();

        return new PageImpl<>(forms, pageable, countResult.total());
    }

    private static List<MatchOperation> createMatchOperations(@Nullable String userId, FormListCriteria filterDto) {
        val operations = new ArrayList<MatchOperation>();

        val publicStatusCriteria = Criteria.where(FormEntity::getStatus).is(FormStatus.PUBLIC);
        if (userId != null) {
            val authorIdCriteria = Criteria.where(FormEntity::getAuthorId).is(userId);
            operations.add(Aggregation.match(new Criteria().orOperator(publicStatusCriteria, authorIdCriteria)));
        } else {
            operations.add(Aggregation.match(publicStatusCriteria));
        }

        if (filterDto.language() != null) {
            operations.add(
                    Aggregation.match(Criteria.where(FormEntity::getLanguage).is(filterDto.language())));
        }

        if (filterDto.minEstimatedDuration() != null || filterDto.maxEstimatedDuration() != null) {
            val estimatedDurationFilterCriteria = Criteria.where(FormEntity::getEstimatedDurationSeconds);

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
            operations.add(Aggregation.match(
                    Criteria.where(FormEntity::getAllowsGuestSubmissions).is(filterDto.allowsGuestSubmissions())));
        }

        if (filterDto.authorId() != null && !filterDto.authorId().isBlank()) {
            operations.add(
                    Aggregation.match(Criteria.where(FormEntity::getAuthorId).is(filterDto.authorId())));
        }

        return Collections.unmodifiableList(operations);
    }

    private static AggregationOperation createTextSearchStage(@Nullable String userId, FormListCriteria filterDto) {
        val compoundParams = new HashMap<String, Object>();

        compoundParams.put(
                "should",
                List.of(
                        Map.of(
                                "autocomplete",
                                Map.of(
                                        QUERY,
                                        filterDto.searchQuery(),
                                        "path",
                                        "name",
                                        "score",
                                        Map.of("boost", Map.of(VALUE, 5)))),
                        Map.of(
                                "text",
                                Map.of(
                                        QUERY,
                                        filterDto.searchQuery(),
                                        "path",
                                        NAME_TEXT_SEARCH_PATH,
                                        "score",
                                        Map.of("boost", Map.of(VALUE, 3)),
                                        "fuzzy",
                                        Map.of("maxEdits", 1, "prefixLength", 2))),
                        Map.of("text", Map.of(QUERY, filterDto.searchQuery(), "path", DESCRIPTION_TEXT_SEARCH_PATH))));
        compoundParams.put("minimumShouldMatch", 1);

        val filters = new ArrayList<Map<String, ?>>();

        if (userId != null) {
            filters.add(Map.of(
                    "compound",
                    Map.of(
                            "should",
                            List.of(
                                    Map.of(EQUALS, Map.of("path", "status", VALUE, FormStatus.PUBLIC.name())),
                                    Map.of(EQUALS, Map.of("path", AUTHOR_ID, VALUE, userId))),
                            "minimumShouldMatch",
                            1)));
        } else {
            filters.add(Map.of(EQUALS, Map.of("path", "status", VALUE, FormStatus.PUBLIC.name())));
        }

        if (filterDto.language() != null) {
            filters.add(Map.of(
                    EQUALS,
                    Map.of("path", "language", VALUE, filterDto.language().name())));
        }

        if (filterDto.allowsGuestSubmissions() != null) {
            filters.add(Map.of(
                    EQUALS, Map.of("path", "allowsGuestSubmissions", VALUE, filterDto.allowsGuestSubmissions())));
        }

        if (filterDto.authorId() != null && !filterDto.authorId().isBlank()) {
            filters.add(Map.of(EQUALS, Map.of("path", AUTHOR_ID, VALUE, filterDto.authorId())));
        }

        if (filterDto.minEstimatedDuration() != null || filterDto.maxEstimatedDuration() != null) {
            val rangeParams = new HashMap<String, Object>();
            rangeParams.put("path", "estimatedDurationSeconds");

            if (filterDto.minEstimatedDuration() != null) {
                rangeParams.put("gte", filterDto.minEstimatedDuration().toSeconds());
            }
            if (filterDto.maxEstimatedDuration() != null) {
                rangeParams.put("lte", filterDto.maxEstimatedDuration().toSeconds());
            }

            filters.add(Map.of("range", rangeParams));
        }

        compoundParams.put("filter", filters);
        val textSearchDocument = new Document("$search", Map.of("compound", compoundParams));

        return Aggregation.stage(textSearchDocument);
    }

    private record CountResult(long total) {}

    private static Sort createValidSort(Sort sort, boolean isTextQuery) {
        val sortOrders = new ArrayList<Sort.Order>();

        if (isTextQuery) sortOrders.add(Sort.Order.desc(SEARCH_SCORE));
        if (sort.isEmpty()) sortOrders.add(Sort.Order.desc(FormEntity::getCreatedAt));

        for (val sortOrder : sort) {
            val field = SORT_FIELDS.get(sortOrder.getProperty().toLowerCase(Locale.ROOT));
            if (field != null) sortOrders.add(new Sort.Order(sortOrder.getDirection(), field.toDotPath()));
        }

        sortOrders.add(Sort.Order.asc(FormEntity::getId));

        return Sort.by(sortOrders);
    }
}

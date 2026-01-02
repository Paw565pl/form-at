package format.backend.comment.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import format.backend.auth.entity.Role;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.service.UserService;
import format.backend.comment.dto.CommentRequestDto;
import format.backend.comment.dto.CommentResponseDto;
import format.backend.comment.entity.CommentEntity;
import format.backend.comment.exception.CommentNotFoundException;
import format.backend.comment.mapper.CommentMapper;
import format.backend.comment.repository.CommentRepository;
import format.backend.comment_rating.entity.CommentRatingEntity;
import format.backend.comment_rating.entity.RatingType;
import format.backend.comment_rating.repository.CommentRatingRepository;
import format.backend.form.service.FormService;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MongoTemplate mongoTemplate;

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final CommentMapper commentMapper;

    private final UserService userService;
    private final FormService formService;

    public CommentEntity findOrThrow(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    public Page<@NonNull CommentResponseDto> findAll(
            String formIdOrSlug, @Nullable KeycloakJwtClaims keycloakJwtClaims, Pageable pageable) {
        val form = formService.findOrThrow(formIdOrSlug);
        val criteria = Criteria.where("formId").is(new ObjectId(form.getId()));

        val total = mongoTemplate.count(Query.query(criteria), CommentEntity.class);
        if (total == 0) return Page.empty(pageable);

        val userId = Optional.ofNullable(keycloakJwtClaims).map(KeycloakJwtClaims::sub);
        val operations = new ArrayList<AggregationOperation>();

        operations.add(Aggregation.match(criteria));
        operations.add(Aggregation.sort(Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.asc("_id"))));
        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        operations.add(Aggregation.lookup("users", "authorId", "_id", "author"));
        operations.add(Aggregation.addFields()
                .addField("authorName")
                .withValue(ArrayOperators.arrayOf("author.username").first())
                .build());

        operations.add(
                Aggregation.addFields()
                        .addField("ratingAvg")
                        .withValue(
                                ArithmeticOperators.Round.roundValueOf(
                                        ConditionalOperators
                                                .when(ComparisonOperators.Eq.valueOf("ratingCount").equalToValue(0))
                                                .then(0.0)
                                                .otherwise(
                                                        ArithmeticOperators.Divide.valueOf("ratingSum").divideBy("ratingCount")
                                                )
                                ).place(1)
                        )
                        .build()
        );

        if (userId.isPresent()) {
            val commentRatingsLookup = LookupOperation.newLookup()
                    .from("comment_ratings")
                    .localField("_id")
                    .foreignField("commentId")
                    .pipeline(Aggregation.match(Criteria.where("authorId").is(userId.get())))
                    .as("userRatings");
            operations.add(commentRatingsLookup);

            val addUserRatingField = Aggregation.addFields()
                    .addField("userRating")
                    .withValue(ArrayOperators.arrayOf("userRatings.type").first())
                    .build();
            operations.add(addUserRatingField);
        }

        val content = mongoTemplate
                .aggregate(Aggregation.newAggregation(operations), CommentEntity.class, CommentResponseDto.class)
                .getMappedResults();

        return new PageImpl<>(content, pageable, total);
    }

    @Transactional
    public CommentResponseDto create(
            String formIdOrSlug, KeycloakJwtClaims keycloakJwtClaims, CommentRequestDto commentRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val user = userService.findOrThrow(keycloakJwtClaims.sub());

        val comment = commentMapper.toEntity(commentRequestDto, form, user);

        val saved = commentRepository.save(comment);
        return commentMapper.toResponseDto(saved, user.getUsername(), null);
    }

    @Transactional
    public CommentResponseDto update(
            String formIdOrSlug,
            String commentId,
            KeycloakJwtClaims keycloakJwtClaims,
            CommentRequestDto commentRequestDto) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val isCommentOwner = Optional.ofNullable(comment.getAuthor())
                .map(a -> Objects.equals(a.getId(), keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isCommentOwner || isAdmin)) throw new ResponseStatusException(FORBIDDEN);

        comment.setContent(commentRequestDto.content());

        val updated = commentRepository.save(comment);
        val authorName = Optional.ofNullable(updated.getAuthor())
                .map(UserEntity::getUsername)
                .orElse(null);

        val userRating = commentRatingRepository
                .findByCommentIdAndAuthorId(updated.getId(), keycloakJwtClaims.sub())
                .map(CommentRatingEntity::getType)
                .flatMap(RatingType::fromValue)
                .orElse(null);

        return commentMapper.toResponseDto(updated, authorName, userRating);
    }

    @Transactional
    public void delete(String formIdOrSlug, String commentId, KeycloakJwtClaims keycloakJwtClaims) {
        val form = formService.findOrThrow(formIdOrSlug);
        val comment = findOrThrow(commentId);

        if (!comment.getForm().getId().equals(form.getId())) throw new ResponseStatusException(NOT_FOUND);

        val isCommentOwner = Optional.ofNullable(comment.getAuthor())
                .map(a -> Objects.equals(a.getId(), keycloakJwtClaims.sub()))
                .orElse(false);
        val isAdmin = keycloakJwtClaims.roles().contains(Role.ADMIN);
        if (!(isCommentOwner || isAdmin)) throw new ResponseStatusException(FORBIDDEN);

        commentRepository.delete(comment);

        commentRatingRepository.deleteAllByCommentId(comment.getId());
    }
}

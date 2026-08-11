package format.backend.auth.domain.repository;

import format.backend.auth.domain.entity.UserEntity;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public UserEntity createOrUpdate(UserEntity userEntity) {
        val now = Instant.now();
        val query = Query.query(Criteria.where(UserEntity::getId).is(userEntity.getId()));
        val update = new Update()
                .set(UserEntity::getUsername, userEntity.getUsername())
                .set(UserEntity::getEmail, userEntity.getEmail())
                .setOnInsert(UserEntity::getCreatedAt, now)
                .set(UserEntity::getUpdatedAt, now);

        return Objects.requireNonNull(mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), UserEntity.class));
    }
}

package format.backend.auth.domain.repository;

import format.backend.auth.domain.entity.UserEntity;

interface UserRepositoryCustom {

    void createOrUpdate(UserEntity userEntity);
}

package format.backend.auth.domain.repository;

import format.backend.auth.domain.entity.UserEntity;

interface UserRepositoryCustom {

    UserEntity createOrUpdate(UserEntity userEntity);
}

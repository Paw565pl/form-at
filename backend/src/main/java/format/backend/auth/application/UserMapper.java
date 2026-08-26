package format.backend.auth.application;

import format.backend.auth.UserClaims;
import format.backend.auth.UserDto;
import format.backend.auth.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(UserEntity userEntity);

    UserEntity toEntity(UserClaims userClaims);
}

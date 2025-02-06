package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "username", source = "user.username"),
            @Mapping(target = "password", source = "user.password"),
            @Mapping(target = "email", source = "user.email")
    })
    UserEntity toEntity(CreateUserDto user);

    @Mappings({
            @Mapping(target = "username", source = "user.username"),
            @Mapping(target = "password", source = "user.password"),
            @Mapping(target = "email", source = "user.email")
    })
    CreateUserDto toDto(UserEntity user);
}

package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Role.Role;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoDro;
import org.example.logisticapplication.domain.User.UserEntity;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "username", source = "user.username"),
            @Mapping(target = "password", source = "encodedPassword"),
            @Mapping(target = "email", source = "user.email"),
            @Mapping(target = "roles", expression = "java(mapRoles())")
    })
    UserEntity toEntity(
            CreateUserDto user,
            String encodedPassword
    );

    default Set<Role> mapRoles() {
        return new HashSet<>();
    }


    @Mappings({
            @Mapping(target = "username", source = "user.username"),
            @Mapping(target = "password", source = "user.password"),
            @Mapping(target = "email", source = "user.email")
    })
    CreateUserDto toDto(UserEntity user);

    @Mappings({
            @Mapping(target = "createdAt", source = "user.createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    MainUserInfoDro toMainInfo(UserEntity user);

}

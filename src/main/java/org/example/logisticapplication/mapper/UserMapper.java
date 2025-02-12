package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Driver.DriverRegistrationInfo;
import org.example.logisticapplication.domain.Role.RoleEntity;
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
            @Mapping(target = "roleEntities", source = "roleEntities")
    })
    UserEntity toEntity(
            CreateUserDto user,
            String encodedPassword,
            Set<RoleEntity> roleEntities
    );


    @Mappings({
            @Mapping(target = "username", expression = "java(driver.getUserName())"),
            @Mapping(target = "email", expression = "java(driver.getEmail())"),
            @Mapping(target = "password", source = "encodedPassword"),
            @Mapping(target = "roleEntities", source = "roleEntities")
    })
    UserEntity toEntity(
            DriverRegistrationInfo driver,
            String encodedPassword,
            Set<RoleEntity> roleEntities
    );

    @AfterMapping
    default void ensureRoles(@MappingTarget UserEntity user) {
        if (user.getRoleEntities() == null) {
            user.setRoleEntities(new HashSet<>());
        }
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

package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Driver.DriverRegistrationInfo;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoDro;
import org.example.logisticapplication.domain.User.UserEntity;
import org.example.logisticapplication.domain.User.UserInfoDto;
import org.mapstruct.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Named("timeFormatter")
    default String dataToStringFormat(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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

    @Mapping(target = "name", source = "user.username")
    @Mapping(target = "createdAt", expression = "java(dataToStringFormat())")
    UserInfoDto toInfoDto(UserEntity user);

}

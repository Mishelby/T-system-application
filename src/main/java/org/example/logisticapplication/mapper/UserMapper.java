package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Driver.DriverRegistrationInfo;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.domain.User.*;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
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
    default String dataToStringFormat() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Mappings({
            @Mapping(target = "username", source = "user.username"),
            @Mapping(target = "password", source = "user.password"),
            @Mapping(target = "email", source = "user.email")
    })
    CreateUserDto toDto(UserEntity user);

    @Mappings({
            @Mapping(target = "userInfoDto.name", source = "user.username"),
            @Mapping(target = "userInfoDto.email", source = "user.email"),
            @Mapping(target = "userInfoDto.createdAt", source = "user.createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(target = "userOrderInfo", expression = "java(null)")
    })
    MainUserInfoWithoutOrdersDto toMainInfoWithoutOrders(UserEntity user);

    MainUserInfoWithOrders toMainInfoWithOrders(
            UserEntity userEntity,
            List<UserOrderEntity> userOrders
    );

    @Condition
    default List<UserOrderEntity> isUserHasOrders(List<UserOrderEntity> orders){
        return orders.isEmpty() ? null : orders;
    }

    @Mapping(target = "name", source = "user.username")
    @Mapping(target = "createdAt", expression = "java(dataToStringFormat())")
    UserInfoDto toInfoDto(UserEntity user);

    @Mapping(target = "name", source = "user.username")
    @Mapping(target = "createdAt", expression = "java(dataToStringFormat())")
    @Mapping(target = "desiredDate", source = "desiredDate")
    UserInfoDto toInfoDto(UserEntity user, String desiredDate);

}

package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.OrderProfileInfo.UserProfileInfoEntity;
import org.example.logisticapplication.domain.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userEntity")
    @Mapping(target = "decided", expression = "java(false)")
    UserProfileInfoEntity toEntity(UserEntity userEntity);

}

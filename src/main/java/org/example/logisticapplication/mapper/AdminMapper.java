package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Admin.AdminEntity;
import org.example.logisticapplication.domain.Admin.BaseAdminDto;
import org.example.logisticapplication.domain.Admin.CreateAdminDto;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminMapper {

    @Mappings({
            @Mapping(target = "username", source = "createAdminDto.username"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "email", source = "createAdminDto.email"),
            @Mapping(target = "roleEntities", source = "roles"),
            @Mapping(target = "name", source = "createAdminDto.name"),
    })
    AdminEntity toEntity(
            CreateAdminDto createAdminDto,
            String password,
            Set<RoleEntity> roles
    );

    @Mappings({
            @Mapping(target = "name", source = "adminEntity.name"),
            @Mapping(target = "message", expression = "java(messageForAdmin(adminEntity))"),
    })
    BaseAdminDto toBaseDto(AdminEntity adminEntity);

    default String messageForAdmin(AdminEntity adminEntity) {
        return "Ключ администратора будет действовать ещё: ";
    }
}

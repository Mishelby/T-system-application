package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.domain.Cargo.CargoDto;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    @Mappings({
            @Mapping(target = "status", source = "cargo.cargoStatus", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL),
            @Mapping(target = "name", source = "cargo.cargoName")
    })
    CargoEntity toEntity(Cargo cargo);

    @Mappings({
            @Mapping(target = "number", source = "cargo.number"),
            @Mapping(target = "name", source = "cargo.name"),
            @Mapping(target = "weightKg", source = "cargo.weightKg"),
            @Mapping(target = "status", source = "cargo.status")
    })
    CargoInfoDto toDtoInfo(CargoEntity cargo);

    Cargo toDomain(CargoEntity entity);

    CargoDto toDto(Cargo cargo);

    Cargo toDomain(CargoDto cargoDto);

}

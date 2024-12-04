package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.domain.Cargo.CargoDto;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    @Mapping(target = "cargoMass", defaultValue = "0")
    @Mapping(target = "cargoStatus", defaultValue = "NOT_READY")
    CargoEntity toEntity(Cargo cargo);

    Cargo toDomain(CargoEntity entity);

    CargoDto toDto(Cargo cargo);

    Cargo toDomain(CargoDto cargoDto);

}

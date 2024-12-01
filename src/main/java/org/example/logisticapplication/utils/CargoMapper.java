package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.domain.Cargo.CargoDto;
import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CargoMapper {

    @Mapping(target = "cargoMass", defaultValue = "0")
    @Mapping(target = "cargoStatus", defaultValue = "NOT_READY")
    CargoEntity toEntity(Cargo cargo);

    Cargo toDomain(CargoEntity entity);

    CargoDto toDto(Cargo cargo);

    Cargo toDomain(CargoDto cargoDto);

}

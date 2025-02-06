package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Cargo.*;
import org.mapstruct.*;

import java.math.BigDecimal;

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

    @Mappings({
            @Mapping(target = "number", source = "cargo.number"),
            @Mapping(target = "weight", source = "cargo.weightKg")
    })
    MainCargoInfoDto toMainInfo(CargoEntity cargo);

    @Mappings({
            @Mapping(target = "name", source = "cargoDto.name"),
            @Mapping(target = "weightKg", source = "cargoDto.weight"),
            @Mapping(target = "number", source = "uniqueNumber")
    })
    CargoEntity toEntity(
            CargoForOrderDto cargoDto,
            String uniqueNumber
    );


    @Mappings({
            @Mapping(target = "number", source = "cargoInfoDto.number"),
            @Mapping(target = "name", source = "cargoInfoDto.name"),
            @Mapping(target = "weightKg", source = "cargoInfoDto.weightKg"),
            @Mapping(target = "status", source = "cargoInfoDto.status")
    })
    CargoEntity toEntity(CargoInfoDto cargoInfoDto);

    @Mappings({
            @Mapping(target = "number", source = "cargoNumber"),
            @Mapping(target = "name", source = "cargoName"),
            @Mapping(target = "weightKg", source = "weightKg"),
            @Mapping(target = "status", source = "cargoStatus")
    })
    CargoInfoDto toInfoDto(
            BigDecimal weightKg,
            String cargoNumber,
            String cargoName,
            String cargoStatus
    );

    Cargo toDomain(CargoEntity entity);

    CargoDto toDto(Cargo cargo);

    @Mapping(target = "number", source = "cargo.number")
    CargoInfoForStatus toInfoStatus(CargoEntity cargo);

    Cargo toDomain(CargoDto cargoDto);

}

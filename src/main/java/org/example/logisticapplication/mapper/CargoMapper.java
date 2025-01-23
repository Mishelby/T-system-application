package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Cargo.*;
import org.example.logisticapplication.utils.CargoNumberGenerator;
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

    @Mappings({
            @Mapping(target = "name", source = "cargoDto.name"),
            @Mapping(target = "weightKg", source = "cargoDto.weight"),
            @Mapping(target = "number", source = "uniqueNumber")
    })
    CargoEntity toEntity(CargoForOrderDto cargoDto, String uniqueNumber);

//    @Mappings({
//            @Mapping(target = "name", source = "cargoDto.name"),
//            @Mapping(target = "weightKg", source = "cargoDto.weight"),
//            @Mapping(target = "number", source = "uniqueNumber")
//    })
//    List<CargoEntity> toEntity(List<CargoForOrderDto> cargoDto, String uniqueNumber);


    @Named("numberForCargo")
    default String numberForCargo() {
        return CargoNumberGenerator.generateNumber();
    }

    @Mappings({
            @Mapping(target = "number", source = "cargoInfoDto.number"),
            @Mapping(target = "name", source = "cargoInfoDto.name"),
            @Mapping(target = "weightKg", source = "cargoInfoDto.weightKg"),
            @Mapping(target = "status", source = "cargoInfoDto.status")
    })
    CargoEntity toEntity(CargoInfoDto cargoInfoDto);

    Cargo toDomain(CargoEntity entity);

    CargoDto toDto(Cargo cargo);

    Cargo toDomain(CargoDto cargoDto);

}

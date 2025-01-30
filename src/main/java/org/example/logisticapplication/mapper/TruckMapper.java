package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Truck.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TruckMapper {

    @Mappings({
            @Mapping(target = "driversShift", defaultValue = "0"),
            @Mapping(target = "status", defaultValue = "SERVICEABLE")
    })
    TruckEntity toEntity(Truck truck);

    @Mappings({
            @Mapping(target = "currentCityId", source = "truck.currentCity.id")
    })
    Truck toDomain(TruckEntity truck);

    TruckDto toDto(Truck truck);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "registrationNumber", source = "entity.registrationNumber"),
            @Mapping(target = "capacity", source = "entity.capacity"),
    })
    TruckForDriverDto toDto(TruckEntity entity);

    Truck toDomain(TruckDto truckDto);

    @Mappings({
            @Mapping(target = "currentCityName", source = "truck.currentCity.name")
    })
    TruckInfoDto toInfoDto(TruckEntity truck);

    @Mappings({
            @Mapping(target = "currentCityName", source = "truck.currentCity.name")
    })
    List<TruckInfoDto> toInfoDto(List<TruckEntity> truck);
}

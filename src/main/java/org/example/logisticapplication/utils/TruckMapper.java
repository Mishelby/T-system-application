package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TruckMapper {

    @Mappings({
            @Mapping(target = "driversShift", defaultValue = "0"),
            @Mapping(target = "status", defaultValue = "SERVICEABLE")
    })
    TruckEntity toEntity(Truck truck);

    Truck toDomain(TruckEntity truck);

    TruckDto toDto(Truck truck);

    Truck toDomain(TruckDto truckDto);
}

package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TruckMapper {

    @Mapping(target = "driversShift", defaultValue = "0")
    @Mapping(target = "status", defaultValue = "SERVICEABLE")
    TruckEntity toEntity(Truck truck);

    Truck toDomain(TruckEntity truck);

    TruckDto toDto(Truck truck);

    Truck toDomain(Truck truck);
}

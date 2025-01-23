package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
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

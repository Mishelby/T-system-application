package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Mappings({
            @Mapping(target = "registrationNumber", source = "truck.registrationNumber"),
            @Mapping(target = "currentCity", source = "truck.currentCity.name"),
            @Mapping(target = "driversInfo", source = "driversInfo"),
            @Mapping(target = "status", source = "truck.status")
    })
    MainTruckInfoDto toMainInfo(
            TruckEntity truck,
            Map<String, Long> driversInfo
    );

    TruckDto toDto(Truck truck);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "registrationNumber", source = "generatedNumber"),
            @Mapping(target = "numberOfSeats", source = "truckDto.numberOfSeats"),
            @Mapping(target = "status", source = "truckDto.status"),
            @Mapping(target = "currentCity", source = "city"),
            @Mapping(target = "capacity", source = "truckDto.capacity")
    })
    TruckEntity toEntity(
            CreateTruckDto truckDto,
            String generatedNumber,
            CityEntity city
    );

    @AfterMapping
    default void defaultListOfDrivers(@MappingTarget TruckEntity truck) {
       if(truck.getDrivers() == null) {
           truck.setDrivers(new ArrayList<>());
       }
    }

    @AfterMapping
    default void setDefaultDriversShift(@MappingTarget TruckEntity truck) {
        if (truck.getDriversShift() == null) {
            truck.setDriversShift(0);
        }
    }

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

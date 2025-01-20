package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.City.CityInfoDto;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    @Mappings({
            @Mapping(target = "status", defaultValue = "SUSPENDED"),
            @Mapping(target = "numberOfHoursWorked", defaultValue = "0")
    })
    DriverEntity toEntity(Driver driver);

    @Mappings({
            @Mapping(target = "currentCityId", source = "currentCity.id"),
            @Mapping(target = "currentTruckId", source = "currentTruck.id")
    })
    Driver toDomain(DriverEntity entity);

    DriverDto toDto(Driver driver);

    Driver toDomain(DriverDto driverDto);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "name", source = "entity.name"),
            @Mapping(target = "secondName", source = "entity.secondName"),
            @Mapping(target = "personNumber", source = "entity.personNumber"),
            @Mapping(target = "numberOfHoursWorked", source = "entity.numberOfHoursWorked"),
            @Mapping(target = "status", source = "entity.status"),
            @Mapping(target = "currentCityInfo", source = "currentCityInfo"),
            @Mapping(target = "currentTruckInfo", source = "currentTruckInfo")
    })
    DriverAllInfoDto toDtoInfo(
            DriverEntity entity,
            CityInfoDto currentCityInfo,
            TruckInfoDto currentTruckInfo
    );

    @Mappings({
            @Mapping(target = "currentLocation", source = "entity.currentCity.name"),
            @Mapping(target = "currentTruckNumber", source = "entity.currentTruck.registrationNumber")
    })
    DriverOrderInfo toOrderInfo(DriverEntity entity);

    @Mappings({
            @Mapping(target = "currentLocation", source = "entity.currentCity.name"),
            @Mapping(target = "currentTruckNumber", source = "entity.currentTruck.registrationNumber")
    })
    List<DriverOrderInfo> toOrderInfo(List<DriverEntity> entity);

}

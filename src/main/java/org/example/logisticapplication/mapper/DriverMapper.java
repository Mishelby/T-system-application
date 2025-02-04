package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.City.CityInfoDto;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public  interface DriverMapper {

    @Mappings({
            @Mapping(target = "status", defaultValue = "SUSPENDED"),
            @Mapping(target = "numberOfHoursWorked", defaultValue = "0")
    })
    DriverEntity toEntity(Driver driver);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "driver.name"),
            @Mapping(target = "status", expression = "java(getDefaultDriverStatus())"),
            @Mapping(target = "numberOfHoursWorked", expression = "java(getDefaultNumberOfHoursWorked())"),
            @Mapping(target = "currentCity", source = "cityEntity")
    })
    DriverEntity toEntity(DriverRegistrationDto driver, CityEntity cityEntity);

    @Named("defaultDriverStatus")
    default String getDefaultDriverStatus() {
        return ShiftStatus.REST.getStatusName();
    }

    @Named("defaultNumberOfHoursWorked")
    default Double getDefaultNumberOfHoursWorked() {
        return 0.0;
    }

    @Mappings({
            @Mapping(target = "currentCityId", source = "currentCity.id"),
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
            @Mapping(target = "name", source = "driverEntity.name"),
            @Mapping(target = "number", source = "driverEntity.personNumber"),
            @Mapping(target = "truckNumber", source = "driverEntity.currentTruck.registrationNumber")
    })
    MainDriverInfoDto toMainInfo(DriverEntity driverEntity);

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

    @Mappings({
            @Mapping(target = "driverId", source = "entity.id"),
            @Mapping(target = "name", source = "entity.name"),
            @Mapping(target = "secondName", source = "entity.secondName"),
            @Mapping(target = "currentCityName", source = "entity.currentCity.name"),
    })
    DriverWithoutTruckDto toDtoWithoutTruck(DriverEntity entity);

}

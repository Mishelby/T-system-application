package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "status", defaultValue = "SUSPENDED")
    @Mapping(target = "numberOfHoursWorked", defaultValue = "0")
    DriverEntity toEntity(Driver driver);

    DriverDto toDto(Driver driver);

    Driver toDomain(DriverDto driverDto);

    Driver toDomain(DriverEntity entity);

}

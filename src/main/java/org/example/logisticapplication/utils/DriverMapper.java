package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverMapper {

    @Mappings({
            @Mapping(target = "status", defaultValue = "SUSPENDED"),
            @Mapping(target = "numberOfHoursWorked", defaultValue = "0")
    })
    DriverEntity toEntity(Driver driver);

    Driver toDomain(DriverEntity entity);

    DriverDto toDto(Driver driver);

    Driver toDomain(DriverDto driverDto);

}

package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.example.logisticapplication.domain.Driver.DriverAndOrderDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverAndOrderMapper {

    @Mappings(value = {
            @Mapping(target = "driver", source = "driverEntity"),
            @Mapping(target = "order", source = "orderEntity"),
            @Mapping(target = "cargo", source = "cargoEntity"),
    })

    DriverAndOrderDto toDto(DriverEntity driverEntity, OrderEntity orderEntity, CargoEntity cargoEntity);
}

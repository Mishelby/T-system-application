package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverOrderMapper {

    @Mappings({
            @Mapping(target = "id", source = "domain.id"),
            @Mapping(target = "order.id", source = "domain.orderId"),
            @Mapping(target = "driver.id", source = "driverEntity.id"),
    })
    DriverOrderEntity toEntity(
            DriverOrder domain,
            OrderEntity orderEntity,
            DriverEntity driverEntity
    );


    DriverOrder toDomain(DriverOrderEntity entity);

    DriverOrderDto toDto(DriverOrder domain);

    DriverOrder toDto(DriverOrderDto dto);
}

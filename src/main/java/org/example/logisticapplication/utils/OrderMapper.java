package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Order.OrderEntity;

import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "orderStatus", defaultValue = "NOT_COMPLETE")
    @Mapping(target = "id", source = "order.id")
    @Mapping(target = "drivers", source = "drivers")
    @Mapping(target = "truck", source = "truck")
    OrderEntity toEntity(Order order, List<DriverEntity> drivers, TruckEntity truck);

    Order toDomain(OrderDto orderDto);

    OrderDto toDto(Order order);

    Order toDomain(OrderEntity entity);


}

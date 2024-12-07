package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderDto;
import org.example.logisticapplication.domain.Order.OrderEntity;

import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

   @Mappings({
           @Mapping(target = "status", source = "order.orderStatus",defaultValue = "NOT_COMPLETE"),
           @Mapping(target = "id", source = "order.id"),
           @Mapping(target = "driverOrders", source = "drivers"),
           @Mapping(target = "truckOrders", source = "truck")
   })
    OrderEntity toEntity(Order order, List<DriverOrderEntity> drivers, List<TruckOrderEntity> truck);

    Order toDomain(OrderDto orderDto);

    OrderDto toDto(Order order);

    Order toDomain(OrderEntity entity);


}

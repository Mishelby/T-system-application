package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TruckOrderMapper {

    @Mappings({
            @Mapping(target = "id", source = "domain.id"),
            @Mapping(target = "order.id", source = "domain.orderId"),
            @Mapping(target = "truck.id", source = "domain.truckId")
    })
    TruckOrderEntity toEntity(
            TruckOrder domain,
            OrderEntity orderEntity,
            TruckEntity truckEntity
    );

    TruckOrder toDomain(TruckOrderEntity entity);

    TruckOrderDto toDto(TruckOrder domain);

    TruckOrder toDomain(TruckOrderDto dto);

}

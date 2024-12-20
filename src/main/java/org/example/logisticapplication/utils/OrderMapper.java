package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrder;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.*;

import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mappings({
            @Mapping(target = "status", source = "orderRequest.orderStatus", defaultValue = "NOT COMPLETED"),
            @Mapping(target = "id", source = "orderRequest.id"),
            @Mapping(target = "countryMap", source = "countryMapEntity"),
            @Mapping(target = "routePoints", source = "routePointEntities")
    })
    OrderEntity toEntity(
            CreateOrderRequest orderRequest,
            CountryMapEntity countryMapEntity,
            List<RoutePointEntity> routePointEntities
    );

    Order toDomain(OrderDto orderDto);

    OrderDto toDto(Order order);

    Order toDomain(OrderEntity entity);

    @Mappings({
            @Mapping(target = "orderStatus", source = "entity.status"),
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName"),
            @Mapping(target = "routePoints", source = "routePointInfoDto"),
            @Mapping(target = "driverOrder", source = "driverOrderInfo"),
            @Mapping(target = "truckOrder", source = "truckInfoDto")
    })
    OrderInfo toDomainInfo(
            OrderEntity entity,
            List<RoutePointInfoDto> routePointInfoDto,
            List<DriverOrderInfo> driverOrderInfo,
            List<TruckInfoDto> truckInfoDto
    );


    OrderInfoDto toDtoInfo(OrderInfo order);

    OrderInfo toDtoInfo(OrderInfoDto order);


}

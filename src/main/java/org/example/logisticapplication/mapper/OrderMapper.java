package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Cargo.MainCargoInfoDto;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.Driver.MainDriverInfoDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriversAndTrucksForOrderDto;
import org.example.logisticapplication.domain.Order.*;

import org.example.logisticapplication.domain.RoutePoint.BaseOrder;
import org.example.logisticapplication.domain.RoutePoint.MainRoutePointInfoDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.MainTruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;


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

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "countryMap", source = "countryMapEntity"),
            @Mapping(target = "routePoints", source = "routePointEntities"),
            @Mapping(target = "driverOrders", source = "driverOrderEntity"),
            @Mapping(target = "truckOrders", source = "truckOrderEntity")
    })
    OrderEntity toEntity(
            String uniqueNumber,
            String status,
            CountryMapEntity countryMapEntity,
            Set<RoutePointEntity> routePointEntities,
            Set<DriverOrderEntity> driverOrderEntity,
            Set<TruckOrderEntity> truckOrderEntity
    );

    @Mappings({
            @Mapping(target = "countryMap.id", source = "countryMapEntity.id"),
            @Mapping(target = "uniqueNumber", source = "baseOrder.uniqueNumber"),
            @Mapping(target = "status", source = "baseOrder.orderStatus"),
            @Mapping(target = "routePoints", source = "routePointEntities"),
            @Mapping(target = "countryMap", source = "countryMapEntity")
    })
    OrderEntity toEntity(
            BaseOrder baseOrder,
            Set<RoutePointEntity> routePointEntities,
            CountryMapEntity countryMapEntity
    );

    @Mappings({
            @Mapping(target = "uniqueNumber", source = "orderEntity.uniqueNumber"),
            @Mapping(target = "orderStatus", source = "orderEntity.status"),
            @Mapping(target = "countyMapName", source = "orderEntity.countryMap.countryName"),
            @Mapping(target = "mainRoutePointInfo", source = "routePointInfo"),
            @Mapping(target = "mainDriverInfo", source = "driverInfo"),
            @Mapping(target = "mainTruckInfo", source = "truckInfo")
    })
    MainOrderInfoDto toMainInfoDto(
            OrderEntity orderEntity,
            List<MainRoutePointInfoDto> routePointInfo,
            List<MainDriverInfoDto> driverInfo,
            List<MainTruckInfoDto> truckInfo
    );

    @Mappings({
            @Mapping(target = "uniqueNumber", source = "entity.uniqueNumber"),
            @Mapping(target = "orderStatus", source = "entity.status"),
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName"),
            @Mapping(target = "routePoints", source = "routePoints"),
    })
    OrderInfo toOrderInfo(
            OrderEntity entity,
            List<RoutePointInfoDto> routePoints
    );

    @Mappings({
            @Mapping(target = "uniqueNumber", source = "entity.uniqueNumber"),
            @Mapping(target = "orderStatus", source = "entity.status"),
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName"),
            @Mapping(target = "routePoints", source = "routePoints"),
            @Mapping(target = "truckOrder", source = "drivers"),
            @Mapping(target = "driverOrder", source = "trucks")
    })
    OrderInfo toOrderInfo(
            OrderEntity entity,
            List<RoutePointInfoDto> routePoints,
            List<DriverOrderInfo> drivers,
            List<TruckInfoDto> trucks
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

    @Mappings({
            @Mapping(target = "orderStatus", source = "entity.status"),
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName"),
            @Mapping(target = "routePoints", source = "routePointInfoDto")
    })
    OrderInfo toDomainInfo(
            OrderEntity entity,
            List<RoutePointInfoDto> routePointInfoDto
    );

    @Mappings({
            @Mapping(target = "orderStatus", source = "entity.status"),
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName"),
            @Mapping(target = "routePoints", source = "routePointInfoDto")
    })
    BaseOrderInfo toBaseOrderInfo(
            OrderEntity entity,
            List<RoutePointInfoDto> routePointInfoDto
    );

    @Mappings({
            @Mapping(target = "drivers", source = "driversDto"),
            @Mapping(target = "trucks", source = "trucksDto")
    })
    DriversAndTrucksForOrderDto toDtoInfo(
            List<DriverAllInfoDto> driversDto,
            List<TruckInfoDto> trucksDto
    );


    OrderInfoDto toDtoInfo(OrderInfo order);

    OrderInfo toDtoInfo(OrderInfoDto order);


}

package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.MainCargoInfoDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.MainDriverInfoDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.MainOrderInfoDto;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.RoutePoint.MainRoutePointInfoDto;
import org.example.logisticapplication.domain.RoutePoint.OperationType;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.MainTruckInfoDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.MainTableInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class MainTableService {
    private final OrderRepository orderRepository;
    private final CargoMapper cargoMapper;
    private final RoutePointMapper routePointMapper;
    private final OrderMapper orderMapper;
    private final DriverMapper driverMapper;
    private final TruckMapper truckMapper;
    private final OrderDistanceRepository orderDistanceRepository;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;


    @Transactional(readOnly = true)
    public MainTableInfoDto getAllInfo() {
        var orderInfoList = getOrderInfos(orderRepository.findAllCurrentOrders());
        var driverInfo = getDriverInfo(driverRepository.findAll());
        var truckInfo = getTruckInfo(truckRepository.findAll());

        return new MainTableInfoDto(
                orderInfoList,
                driverInfo,
                truckInfo,
                (long) (driverInfo.isEmpty() ? 0 : driverInfo.size()),
                (long) (truckInfo.isEmpty() ? 0 : truckInfo.size())
        );
    }

    private List<MainOrderInfoDto> getOrderInfos(
            List<OrderEntity> orderEntities
    ) {
        return orderEntities.stream()
                .map(order -> {
                    var routePoints = getRoutePointInfoDto(order);
                    var drivers = getDriverInfoDto(order);
                    var trucks = getTruckInfo(order);

                    return orderMapper.toMainInfoDto(order, routePoints, drivers, trucks);
                }).toList();
    }

    private List<MainDriverInfoDto> getDriverInfo(
            List<DriverEntity> drivers
    ) {
        return drivers.stream()
                .map(driverMapper::toMainInfo)
                .toList();
    }

    private List<MainTruckInfoDto> getTruckInfo(
            List<TruckEntity> trucks
    ) {
        return trucks.stream()
                .map(truck -> {
                    var driverInfo = new HashMap<String, Long>();
                    truck.getDrivers().forEach(driver -> {
                        driverInfo.putIfAbsent(driver.getName(), driver.getPersonNumber());
                    });

                    return truckMapper.toMainInfo(truck, driverInfo);
                }).toList();
    }

    private List<MainRoutePointInfoDto> getRoutePointInfoDto(
            OrderEntity order
    ) {
        return order.getRoutePoints()
                .stream()
                .filter(rp -> rp.getOperationType().equals(OperationType.LOADING.name()))
                .map(rp -> {
                    var cityFrom = getCityEntity(order, OperationType.LOADING);
                    var cityTo = getCityEntity(order, OperationType.UNLOADING);
                    var distance = getDistance(order);
                    var cargos = getCargoInfoDto(rp);

                    return routePointMapper.toMainInfo(
                            cityFrom,
                            cityTo,
                            distance,
                            !cargos.isEmpty()
                                    ? cargos.getFirst()
                                    : null
                    );
                }).toList();
    }

    private List<MainCargoInfoDto> getCargoInfoDto(
            RoutePointEntity rp
    ) {
        return rp.getCargo()
                .stream()
                .map(cargoMapper::toMainInfo)
                .toList();
    }

    private static String getCityEntity(
            OrderEntity order,
            OperationType type
    ) {
        return order.getRoutePoints().stream()
                .filter(rop -> rop.getOperationType().equals(type.name()))
                .map(RoutePointEntity::getCity)
                .findFirst()
                .map(CityEntity::getName)
                .get();
    }

    private Long getDistance(
            OrderEntity order
    ) {
        return orderDistanceRepository.findDistanceEntityByOrder(order.getId()).orElseThrow(
                () -> new EntityNotFoundException("No distance for order with id = %s"
                        .formatted(order.getId()))
        ).getDistance();

    }

    private List<MainTruckInfoDto> getTruckInfo(
            OrderEntity order
    ) {
        return order.getTruckOrders().stream()
                .map(TruckOrderEntity::getTruck)
                .map(truckEntity -> {
                    var driverInfo = new HashMap<String, Long>();
                    truckEntity.getDrivers().forEach(driver ->
                            driverInfo.putIfAbsent(driver.getName(), driver.getPersonNumber())
                    );

                    return truckMapper.toMainInfo(truckEntity, driverInfo);
                })
                .toList();
    }

    private List<MainDriverInfoDto> getDriverInfoDto(
            OrderEntity order
    ) {
        return order.getDriverOrders()
                .stream()
                .map(DriverOrderEntity::getDriver)
                .map(driverMapper::toMainInfo)
                .toList();
    }

}

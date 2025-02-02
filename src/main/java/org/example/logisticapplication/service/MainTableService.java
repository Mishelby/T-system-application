package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverAllInfoDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderInfo;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.MainTableInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MainTableService {
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final CargoMapper cargoMapper;
    private final RoutePointMapper routePointMapper;
    private final OrderMapper orderMapper;
    private final DriverMapper driverMapper;
    private final TruckMapper truckMapper;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;


    @Transactional(readOnly = true)
    public MainTableInfoDto getAllInfo() {
        var orderEntities = orderRepository.findAll();
        var driversList = driverRepository.findAll();
        var truckList = truckRepository.findAll();

        var allDriversInfo = getDriverAllInfoDto(driversList);
        var truckAllInfo = getTruckAllInfo(truckList);
        var orderInfoList = getOrderInfos(orderEntities);

        return new MainTableInfoDto(
                orderInfoList,
                allDriversInfo,
                truckAllInfo
        );
    }

    private List<TruckInfoDto> getTruckAllInfo(
            List<TruckEntity> truckList
    ) {
       return truckList.stream()
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private List<OrderInfo> getOrderInfos(
            List<OrderEntity> orderEntities
    ) {
        return orderEntities.stream()
                .map(order -> {
                    var routePoints = getRoutePointInfoDto(order);
                    var drivers = getDriverOrderInfos(order);
                    var trucks = getTruckInfoDto(order);

                    return orderMapper.toOrderInfo(order, routePoints, drivers, trucks);
                }).toList();
    }

    private List<DriverAllInfoDto> getDriverAllInfoDto(
            List<DriverEntity> driversList
    ) {
        return driversList
                .stream()
                .map(driver -> {
                    var cityEntity = cityRepository.findCityEntityByName(driver.getCurrentCity()
                                    .getName())
                            .orElseThrow();

                    var currentTruck = driver.getCurrentTruck();
                    var truckInfoDto = truckMapper.toInfoDto(currentTruck);
                    var cityInfo = cityMapper.toInfoDto(cityEntity);

                    return driverMapper.toDtoInfo(driver, cityInfo, truckInfoDto);
                }).toList();
    }

    private List<RoutePointInfoDto> getRoutePointInfoDto(
            OrderEntity order
    ) {
        return order.getRoutePoints()
                .stream()
                .map(rp -> {
                    var cargos = rp.getCargo()
                            .stream()
                            .map(cargoMapper::toDtoInfo)
                            .collect(Collectors.toSet());

                    return routePointMapper.toInfoDto(rp, cargos);
                }).toList();
    }

    private List<TruckInfoDto> getTruckInfoDto(
            OrderEntity order
    ) {
        return order.getTruckOrders()
                .stream()
                .map(TruckOrderEntity::getTruck)
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private List<DriverOrderInfo> getDriverOrderInfos(
            OrderEntity order
    ) {
        return order.getDriverOrders()
                .stream()
                .map(DriverOrderEntity::getDriver)
                .map(driverMapper::toOrderInfo)
                .toList();
    }


}

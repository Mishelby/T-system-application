package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverAndTruckDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.OrderInfo;
import org.example.logisticapplication.domain.Order.OrderInfoDto;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderInfoService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RoutePointMapper routePointMapper;
    private final CargoMapper cargoMapper;
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final TruckMapper truckMapper;
    private final CityMapper cityMapper;

    @Transactional(readOnly = true)
    public List<OrderInfo> findLastOrders(
            Integer countOfLastOrders
    ) {

        Pageable pageable = PageRequest.of(0, countOfLastOrders, Sort.by(Sort.Direction.DESC, "id"));
        var lastOrders = orderRepository.findLast(pageable);

        return lastOrders.stream()
                .map(entity -> {
                    var routePointInfoDtoList = entity.getRoutePoints().stream()
                            .map(routePoint -> {
                                var cargoInfoDto = routePoint.getCargo().stream()
                                        .map(cargoMapper::toDtoInfo)
                                        .toList();

                                return routePointMapper.toInfoDto(routePoint, cargoInfoDto);
                            }).toList();
                    return orderMapper.toDomainInfo(entity, routePointInfoDtoList);
                }).toList();
    }

    @Transactional(readOnly = true)
    public DriverAndTruckDto findAllDriversAndTrucks() {

        var driverDomainList = driverRepository.findAllDrivers().stream()
                .map(driver ->
                        driverMapper.toDtoInfo(
                                driver,
                                cityMapper.toInfoDto(driver.getCurrentCity()),
                                truckMapper.toInfoDto(driver.getCurrentTruck()))
                ).toList();

        var truckDomainList = truckRepository.findAllTrucks().stream()
                .map(truckMapper::toDomain)
                .map(truckMapper::toDto)
                .toList();

        return new DriverAndTruckDto(driverDomainList, truckDomainList);
    }
}

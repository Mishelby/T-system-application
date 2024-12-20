package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverAndTruckDto;
import org.example.logisticapplication.domain.Driver.DriverOrderInfo;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderInfo;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
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
import java.util.stream.Collectors;

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

        Pageable pageable = PageRequest.of(
                0,
                countOfLastOrders != null ? countOfLastOrders : 3,
                Sort.by(Sort.Direction.DESC, "id"));

        var lastOrders = orderRepository.findLast(pageable);

        var driverOrderInfos = getDriverOrderInfo(lastOrders);
        var truckInfoDto = getTruckInfoDto(lastOrders);

        return lastOrders.stream()
                .map(entity -> {
                    var routePointInfoDtoList = entity.getRoutePoints().stream()
                            .map(routePoint -> {
                                var cargoInfoDto = routePoint.getCargo().stream()
                                        .map(cargoMapper::toDtoInfo)
                                        .collect(Collectors.toSet());

                                return routePointMapper.toInfoDto(routePoint, cargoInfoDto);
                            }).toList();
                    return orderMapper.toDomainInfo(entity, routePointInfoDtoList, driverOrderInfos, truckInfoDto);
                }).toList();
    }

    private List<TruckInfoDto> getTruckInfoDto(List<OrderEntity> lastOrders) {
        return lastOrders.stream()
                .flatMap(entity -> entity.getTruckOrders().stream())
                .map(TruckOrderEntity::getTruck)
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private List<DriverOrderInfo> getDriverOrderInfo(List<OrderEntity> lastOrders) {
        return lastOrders.stream()
                .flatMap(entity -> entity.getDriverOrders().stream())
                .map(DriverOrderEntity::getDriver)
                .map(driverMapper::toOrderInfo)
                .toList();
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

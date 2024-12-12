package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderStatusDto;
import org.example.logisticapplication.domain.RoutePoint.OperationType;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderValidHelper orderValidHelper;
    private final CountryMapRepository countryMapRepository;
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final RoutePointRepository routePointRepository;

    @Transactional
    public Order createBaseOrder(
            CreateOrderRequest orderRequest
    ) {
        orderValidHelper.isOrderHasBeenCreated(orderRequest);

        var countryMapEntity = countryMapRepository.findById(orderRequest.countyMapId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Country Map with id=%s Not Found"
                                .formatted(orderRequest.countyMapId())
                )
        );

        var routePointEntities = orderValidHelper.getRoutePointEntities(orderRequest);

        Set<Long> loadedCargos = OrderValidHelper.getIdForLoadedCargos(routePointEntities);
        Set<Long> unloadedCargos = OrderValidHelper.getIdForUnloadedCargos(routePointEntities);

        if (!loadedCargos.equals(unloadedCargos)) {
            throw new IllegalArgumentException(
                    "Not all cargos for order =%s are properly loaded and unloaded"
                            .formatted(orderRequest.uniqueNumber())
            );
        }

        var orderEntity = orderMapper.toEntity(orderRequest, countryMapEntity, routePointEntities);

        routePointEntities.forEach(entity -> entity.setOrder(orderEntity));
        orderRepository.save(orderEntity);

        return orderMapper.toDomain(orderEntity);
    }


    @Transactional(readOnly = true)
    public List<Truck> findTruckForOrder(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        var correctTrucks = truckRepository.findAllCorrectTrucks(
                TruckStatus.SERVICEABLE.toString(),
                orderId
        );

        return correctTrucks.stream()
                .map(truckMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatusById(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        return orderRepository.showOrderStatusByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<Driver> showDriversForOrder(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        var routePointEntity = routePointRepository.findRoutePointEntitiesByOrderId(
                orderId,
                OperationType.LOADING.toString()
        );

        var driversForCorrectTruck = driverRepository.findDriversForCorrectTruck(
                routePointEntity.getCity().getId(),
                orderId
        );

        return driversForCorrectTruck
                .stream()
                .map(driverMapper::toDomain)
                .toList();
    }
}

package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverDefaultValues;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.CreateOrderRequest;
import org.example.logisticapplication.domain.Order.Order;
import org.example.logisticapplication.domain.Order.OrderStatusDto;
import org.example.logisticapplication.domain.RoutePoint.OperationType;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
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
    private final DriverDefaultValues defaultValues;

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
    public List<Truck> findTrucksForOrder(
            List<RoutePointEntity> routePointEntities
    ) {

        var routePointsIdList = routePointEntities.stream().map(RoutePointEntity::getId).toList();

        var loadingCityId = routePointEntities.stream()
                .filter(rp -> rp.getOperationType().equals(OperationType.LOADING.toString()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No loading operation found"))
                .getCity()
                .getId();


        var correctTrucks = truckRepository.findTrucksForOrder(
                routePointsIdList,
                loadingCityId,
                TruckStatus.SERVICEABLE.toString()
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
    public List<Driver> findDriversForOrder(
            Long orderId
    ) {
        orderValidHelper.validateOrderAndFetch(orderId);

        var routePointEntity = routePointRepository.findRoutePointEntitiesByOrderId(
                orderId,
                OperationType.LOADING.toString()
        );

        var driversForCorrectTruck = driverRepository.findDriversForCorrectTruck(
                routePointEntity.getCity().getId(),
                orderId,
                defaultValues.getAverageSpeed(),
                defaultValues.getNumberOfHoursWorkedLimit()
        );

        return driversForCorrectTruck
                .stream()
                .map(driverMapper::toDomain)
                .toList();
    }

    @Transactional
    public Order appointTruckAndDrivers(
            Long orderId,
            Long driverId
    ) {

        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order with id=%s Not Found"
                    .formatted(orderId));
        }

        var driverWithTruckDto = driverRepository.findDriverWithTruckById(driverId).orElseThrow(
                () -> new EntityNotFoundException("Driver with id=%s Not Found"
                        .formatted(driverId))
        );

        var orderEntity = orderRepository.getReferenceById(orderId);

        orderEntity.getDriverOrders().add(new DriverOrderEntity(orderEntity, driverWithTruckDto.driver()));
        orderEntity.getTruckOrders().add(new TruckOrderEntity(orderEntity, driverWithTruckDto.truck()));

        orderRepository.save(orderEntity);
        return orderMapper.toDomain(orderEntity);
    }
}

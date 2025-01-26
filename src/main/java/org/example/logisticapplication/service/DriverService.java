package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.CargoInfoDto;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderMainInfo;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.RoutePoint.RoutePointInfoDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckInfoDto;
import org.example.logisticapplication.domain.TruckOrderEntity.TruckOrderEntity;
import org.example.logisticapplication.mapper.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final CityRepository cityRepository;
    private final OrderRepository orderRepository;
    private final RoutePointMapper routePointMapper;
    private final CargoMapper cargoMapper;
    private final TruckMapper truckMapper;

    @Transactional
    public Driver createDriver(
            DriverRegistrationDto driver
    ) {
        if (driverRepository.existsByPersonNumber(driver.personNumber())) {
            throw new IllegalArgumentException(
                    "Driver already exists with person number=%s"
                            .formatted(driver.personNumber())
            );
        }

        var cityEntity = cityRepository.findById(driver.currentCityId()).
                orElseThrow();

        var driverEntity = driverMapper.toEntity(driver, cityEntity);


        return driverMapper.toDomain(
                driverRepository.save(driverEntity)
        );
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll(
            String status,
            String cityName
    ) {
        var allDrivers = driverRepository.findAllDrivers(status, cityName);

        if (allDrivers.isEmpty()) {
            return new ArrayList<>();
        }

        return allDrivers
                .stream()
                .map(driverMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Driver findById(
            Long id
    ) {
        return driverMapper.toDomain(
                driverRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException(
                                DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
                        )
                ));
    }

    @Transactional
    public void deleteDriver(
            Long id
    ) {
        var driverEntity = driverRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
                )
        );

        var allByCurrentDriver = truckRepository.findAllByCurrentDriver(driverEntity);

        for (TruckEntity truckEntity : allByCurrentDriver) {
            truckEntity.getDrivers().remove(driverEntity);
        }

        driverEntity.setCurrentTruck(null);
        driverRepository.delete(driverEntity);
    }

    @Transactional
    public Driver updateDriver(
            Long id,
            Driver updateDriver
    ) {
        if (!driverRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    DriverValidHelper.getDEFAULT_MESSAGE().formatted(id)
            );
        }

        driverRepository.updateCurrentDriver(
                id,
                updateDriver.name(),
                updateDriver.secondName(),
                updateDriver.personNumber(),
                updateDriver.numberOfHoursWorked(),
                updateDriver.status(),
                updateDriver.currentCityId(),
                updateDriver.currentTruckId()
        );

        return driverMapper.toDomain(
                driverRepository.findById(id).orElseThrow()
        );
    }

    @Transactional(readOnly = true)
    public DriverInfo getDriverInfo(
            Long driverId
    ) {
        var driverEntity = driverRepository.findById(driverId).orElseThrow();
        var orderEntity = orderRepository.findOrderEntitiesByDriverId(driverEntity.getId()).orElse(null);

        if (orderEntity == null) {
            return new DriverMainInfoWithoutOrder(
                    driverEntity.getName(),
                    driverEntity.getPersonNumber().toString(),
                    "No orders"
            );
        }

        var routePointInfoDto = getRoutePointInfoDto(orderEntity, driverEntity);
        var truckInfoDto = getTruckInfoDto(orderEntity);
        var orderMainInfo = getOrderMainInfo(orderEntity, routePointInfoDto, truckInfoDto);

        return new DriverMainInfoDto(
                driverEntity.getName(),
                driverEntity.getPersonNumber().toString(),
                orderMainInfo
        );
    }

    @Transactional(readOnly = true)
    public DriverInfoDto getInfoForDriver(
            Long driverId
    ) {
        var driverEntity = driverRepository.findById(driverId).orElseThrow();
        var orderEntity = getOrderEntity(driverId);

        var allDriverInOrder = orderEntity.getDriverOrders()
                .stream()
                .filter(driverOrderEntity -> !driverOrderEntity.getDriver().getId().equals(driverId))
                .flatMap(dor -> dor.getDriver().getCurrentTruck().getDrivers().stream())
                .toList();

        var firstDriver = allDriverInOrder.getFirst();

        return new DriverInfoDto(
                driverEntity.getPersonNumber().toString(),
                getNumbersAnotherDrivers(allDriverInOrder),
                firstDriver.getCurrentTruck().getRegistrationNumber(),
                orderEntity.getUniqueNumber(),
                getRoutePointInfoDto(orderEntity, driverEntity)
        );
    }

    private static OrderMainInfo getOrderMainInfo(
            OrderEntity orderEntity,
            List<RoutePointInfoDto> routePointInfoDto,
            List<TruckInfoDto> truckInfoDto
    ) {
        return new OrderMainInfo(
                orderEntity.getUniqueNumber(),
                orderEntity.getStatus(),
                orderEntity.getCountryMap().getCountryName(),
                routePointInfoDto,
                truckInfoDto
        );
    }

    private List<TruckInfoDto> getTruckInfoDto(
            OrderEntity orderEntity
    ) {
        return orderEntity.getTruckOrders()
                .stream()
                .map(TruckOrderEntity::getTruck)
                .map(truckMapper::toInfoDto)
                .toList();
    }

    private OrderEntity getOrderEntity(Long orderId) {
        return orderRepository.findOrderEntitiesByDriverId(orderId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Order does not exist with id = %s"
                                .formatted(orderId)
                )
        );
    }

    private List<RoutePointInfoDto> getRoutePointInfoDto(
            OrderEntity orderEntity,
            DriverEntity driverEntity
    ) {
        var distanceEntity = orderEntity.getCountryMap().getDistances()
                .stream()
                .filter(distances -> distances.getFromCity().getId().equals(driverEntity.getCurrentCity().getId()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("No distances for city with %s and %s")
                );

        var routePointInfoDto = orderEntity.getRoutePoints().stream()
                .map(rp -> {
                    var cargoInfoDtoSet = rp.getCargo().stream()
                            .map(cargoMapper::toDtoInfo)
                            .collect(Collectors.toSet());

                    return routePointMapper.toInfoDto(rp, cargoInfoDtoSet);
                })
                .toList();

        routePointInfoDto.forEach(rp -> rp.setDistance(distanceEntity.getDistance()));

        return routePointInfoDto;
    }

    private static Set<String> getNumbersAnotherDrivers(List<DriverEntity> drivers) {
        return drivers.stream()
                .map(DriverEntity::getPersonNumber)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

}

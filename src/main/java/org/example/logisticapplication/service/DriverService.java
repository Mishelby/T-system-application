package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.mapper.DriverMapper;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final CityRepository cityRepository;
    private final OrderRepository orderRepository;

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

//        if(!driverRepository.existsTruckByDriverId(
//                driverEntity.getPersonNumber(),
//                driver.currentTruckName())
//        ){
//            throw new IllegalArgumentException("Truck already exists for driver with id=%s"
//                    .formatted(driverEntity.getId()));
//        }else{
//            var truckEntity = truckRepository.findByNumber(driver.currentTruckName()).orElseThrow();
//            driverEntity.setCurrentTruck(truckEntity);
//            truckEntity.setDrivers(List.of(driverEntity));
//        }

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
    public DriverInfoDto getInfoForDriver(
            Long orderId
    ) {

        var orderEntity = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Order does not exist with id=%s"
                                .formatted(orderId)
                )
        );

        var driversForOrderId = driverRepository.findDriversForOrderId(orderId);

        if (orderEntity.getDriverOrders().size() != driversForOrderId.size()) {
            throw new IllegalArgumentException(
                    "Order does not contain drivers with id=%s"
                            .formatted(orderId)
            );
        }

        var firstDriver = driversForOrderId.getFirst();

        return new DriverInfoDto(
                firstDriver.getPersonNumber().toString(),
                firstDriver.getCurrentTruck().getDrivers()
                        .stream()
                        .filter(driver -> !driver.getId().equals(firstDriver.getId()))
                        .map(DriverEntity::getPersonNumber)
                        .map(String::valueOf).collect(Collectors.toSet()),
                firstDriver.getCurrentTruck().getRegistrationNumber(),
                orderEntity.getUniqueNumber(),
                orderEntity.getRoutePoints().stream().map(RoutePointEntity::getId).toList()
        );
    }

}

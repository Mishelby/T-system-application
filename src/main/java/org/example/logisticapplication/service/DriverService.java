package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.Truck.TruckDtoConverter;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverEntityConverter entityConverter;
    private final TruckRepository truckRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Driver createDriver(
            Driver driver
    ) {
        if (driverRepository.existsByPersonNumber(driver.personNumber())) {
            throw new IllegalArgumentException(
                    "Driver already exists with person number=%s"
                            .formatted(driver.personNumber())
            );
        }

        var savedDriver = driverRepository.save(
                entityConverter.toEntity(driver)
        );

        return entityConverter.toDomain(savedDriver);
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll() {
        var allDrivers = driverRepository.findAll();

        if (allDrivers.isEmpty()) {
            return new ArrayList<>();
        }

        return allDrivers
                .stream()
                .map(entityConverter::toDomain)
                .toList();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public Driver findById(
            Long id
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }

        var driverEntity = driverRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        "Driver does not exist with id=%s"
                                .formatted(id)
                )
        );

        return entityConverter.toDomain(driverEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteDriver(
            Long id
    ) {
        if (id == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }

        var driverEntity = driverRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        "Driver does not exist with id=%s"
                                .formatted(id)
                )
        );

        var allByCurrentDriver = truckRepository.findAllByCurrentDriver(driverEntity);

        for (TruckEntity truckEntity : allByCurrentDriver) {
            truckEntity.getDrivers().remove(driverEntity);
        }

        driverEntity.setCurrentTruck(null);
        driverRepository.delete(driverEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Driver updateDriver(
            Long id,
            Driver updateDriver
    ) {
        var driverById = findById(id);

        var updatedDriver = new Driver(
                driverById.id(),
                orDefault(updateDriver.name(), driverById.name()),
                orDefault(updateDriver.secondName(), driverById.secondName()),
                orDefault(updateDriver.personNumber(), driverById.personNumber()),
                orDefault(updateDriver.numberOfHoursWorked(), driverById.numberOfHoursWorked()),
                orDefault(updateDriver.status(), driverById.status()),
                orDefault(updateDriver.currentCityId(), driverById.currentCityId()),
                orDefault(updateDriver.currentTruckId(), driverById.currentTruckId())
        );

        driverRepository.save(
                entityConverter.toEntity(updatedDriver)
        );

        return updatedDriver;
    }

    private <T> T orDefault(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}

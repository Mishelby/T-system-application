package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.*;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.DriverMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;
    private final CityRepository cityRepository;

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
                driverMapper.toEntity(driver)
        );


        savedDriver.setCurrentCity(
                cityRepository.findById(driver.currentCityId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No City found with id=%s"
                                        .formatted(driver.currentCityId())
                        )
                )
        );

        return driverMapper.toDomain(savedDriver);
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll(String status, String cityName) {
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
        if (id == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }

        var driverEntity = driverRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        "Driver does not exist with id=%s"
                                .formatted(id)
                )
        );

        return driverMapper.toDomain(driverEntity);
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
                driverMapper.toEntity(updatedDriver)
        );

        return updatedDriver;
    }

    private <T> T orDefault(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }
}

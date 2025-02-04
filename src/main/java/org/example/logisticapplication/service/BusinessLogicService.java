package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.DriverShift.DriverShift;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.DriverShiftRepository;
import org.example.logisticapplication.utils.BusinessLogicHelper;
import org.example.logisticapplication.mapper.DriverMapper;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BusinessLogicService implements DriverLogicService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverValidHelper driverValidHelper;
    private final DriverShiftRepository driverShiftRepository;
    private final ShiftSchedulerService shiftSchedulerService;

    private final Clock clock = Clock.systemDefaultZone();


    @Override
    @Transactional
    public Driver addTruckForDriver(
            Long driverId,
            Long truckId
    ) {

        var driverEntity = driverValidHelper.findDriverById(driverId);
        var truckEntity = driverValidHelper.findTruckById(truckId);

        driverValidHelper.validateDriverNotAssignedToTruck(driverEntity, truckEntity);
        driverValidHelper.validateTruckBelongsToDriverCity(driverEntity, truckEntity);
        driverValidHelper.validateTruckHasAvailableSeats(truckEntity);

        truckEntity.getDrivers().add(driverEntity);
        driverEntity.setCurrentTruck(truckEntity);

        driverRepository.save(driverEntity);

        return driverMapper.toDomain(driverEntity);
    }

    @Override
    @Transactional
    public void changeShiftStatus(
            Long driverId,
            String status
    ) {
        BusinessLogicHelper.isValidShiftStatus(status);

        var driverEntity = getDriverEntity(driverId);
        var currentShift = getDriverShift(driverId);

        switch (ShiftStatus.valueOf(status)) {
            case ON_SHIFT -> {
                if (currentShift != null && currentShift.getEndShift() == null) {
                    throw new IllegalArgumentException("Driver already on shift: %s".formatted(driverId));
                }

                var newShift = new DriverShift(driverEntity, LocalDateTime.now(clock));

                driverShiftRepository.save(newShift);
                shiftSchedulerService.autoCloseExpiredShifts(currentShift);
            }
            case REST -> {
                if (currentShift == null) {
                    throw new IllegalArgumentException("No active shift for driver: %s".formatted(driverId));
                }
                if (currentShift.getEndShift() != null) {
                    throw new IllegalArgumentException("Driver already rested: %s".formatted(driverId));
                }

                var endShift = LocalDateTime.now(clock);
                updateShiftAndDriver(currentShift, endShift, driverEntity);

                driverShiftRepository.save(currentShift);
                driverRepository.save(driverEntity);
            }
            default -> throw new IllegalArgumentException("Invalid shift status: %s".formatted(status));
        }

        driverRepository.changeShiftForDriverById(driverId, status);
    }

    private static void updateShiftAndDriver(
            DriverShift currentShift,
            LocalDateTime endShift,
            DriverEntity driverEntity
    ) {
        Double durationOnShift = getDurationOnShift(currentShift, endShift);

        currentShift.setEndShift(endShift);
        currentShift.setHoursWorked(durationOnShift);
        driverEntity.setNumberOfHoursWorked(
                driverEntity.getNumberOfHoursWorked()  + durationOnShift
        );
    }

    static Double getDurationOnShift(
            DriverShift currentShift,
            LocalDateTime endShift
    ) {
        var workedDuration = Duration.between(currentShift.getStartShift(), endShift);
        var hours = workedDuration.toHours();
        var minutes = hours >= 1.0
                ? hours + workedDuration.toMinutes() % 60
                : workedDuration.toMinutes();
        var totalTime = hours + (minutes / 60.0);
        var roundedTime = BigDecimal.valueOf(totalTime).setScale(1, RoundingMode.HALF_UP);

        return roundedTime.doubleValue();
    }

    private DriverShift getDriverShift(
            Long driverId
    ) {
        return driverShiftRepository.findByDriverId(driverId).orElse(null);
    }

    private DriverEntity getDriverEntity(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(
                () -> new EntityNotFoundException("Driver with id=%s not found".formatted(driverId))
        );
    }

    @Transactional
    public void changeDriverStatus(
            Long driverId,
            String status
    ) {
        BusinessLogicHelper.isValidDriverStatus(status);

        var driverEntity = driverRepository.findById(driverId).orElseThrow();

        if (driverEntity.getStatus().equals(ShiftStatus.REST.getStatusName())) {
            throw new IllegalArgumentException("A driver on vacation cannot change their status.: %s"
                    .formatted(driverId));
        }

        var anotherDrivers = driverEntity.getCurrentTruck().getDrivers()
                .stream()
                .filter(driver -> !driver.getId().equals(driverId))
                .toList();

        if (!anotherDrivers.isEmpty() && anotherDrivers
                .stream()
                .anyMatch(anotherDriver -> anotherDriver.getStatus().equals(status))
        ) {
            throw new IllegalArgumentException("Only one driver in truck can have driving status: %s"
                    .formatted(status));
        }

        driverRepository.changeDriverStatusById(driverId, status);
    }

}

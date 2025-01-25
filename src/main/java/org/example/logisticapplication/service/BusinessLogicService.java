package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverShift.DriverShift;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.DriverShiftRepository;
import org.example.logisticapplication.utils.BusinessLogicHelper;
import org.example.logisticapplication.mapper.DriverMapper;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public Driver addTruckForDriver(
            Long driverId,
            Long truckId
    ) {

        /**
         *  Find driver and truck by id
         */
        var driverEntity = driverValidHelper.findDriverById(driverId);
        var truckEntity = driverValidHelper.findTruckById(truckId);

        /**
         * Validation for checking:
         * the driver is not in this truck,
         * the driver and the truck are in the same city,
         * there are places in the truck
         */
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
        /**
         * Check correct status for driver {correct: ON_SHIFT, REST}
         */
        BusinessLogicHelper.isValidShiftStatus(status);

        var driverEntity = getDriverEntity(driverId);
        var currentShift = getDriverShift(driverId);

        switch (ShiftStatus.valueOf(status)) {
            case ON_SHIFT -> {
                if (currentShift != null && currentShift.getEndShift() == null) {
                    throw new IllegalArgumentException("Driver already on shift: %s".formatted(driverId));
                }

                var newShift = new DriverShift(driverEntity, LocalDateTime.now());

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

                var endShift = LocalDateTime.now();
                currentShift.setEndShift(endShift);

                long workedMinutes = Duration.between(currentShift.getStartShift(), endShift).toMinutes();
                int extraHours = workedMinutes % 60 >= 30 ? 1 : 0;
                driverEntity.setNumberOfHoursWorked(
                        driverEntity.getNumberOfHoursWorked() + (int) (workedMinutes / 60) + extraHours
                );

                driverRepository.save(driverEntity);
            }
            default -> throw new IllegalArgumentException("Invalid shift status: %s".formatted(status));
        }

        driverRepository.changeShiftForDriverById(driverId, status);
    }

    private DriverShift getDriverShift(Long driverId) {
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
        /**
         * Check correct status for driver {correct: DRIVING, THE_SECOND_DRIVER, LOADING_UNLOADING_OPERATIONS}
         */
        BusinessLogicHelper.isValidDriverStatus(status);
        driverRepository.changeDriverStatusById(driverId, status);
    }

}

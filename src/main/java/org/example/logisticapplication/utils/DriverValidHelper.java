package org.example.logisticapplication.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class DriverValidHelper {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;

    @Getter
    @Value("${truck.default-seats}")
    private int defaultTruckSeats;

    @Getter
    private static final String DEFAULT_MESSAGE = "Driver does not exist with id=%s";

    @Transactional(readOnly = true)
    public TruckEntity findTruckById(
            Long truckId
    ) {
        return truckRepository.findById(truckId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Truck does not exist with id=%s"
                                .formatted(truckId)
                )
        );
    }

    @Transactional(readOnly = true)
    public DriverEntity findDriverById(
            Long driverId
    ) {
        return driverRepository.findById(driverId).orElseThrow(
                () -> new IllegalArgumentException(
                        DEFAULT_MESSAGE.formatted(driverId)
                )
        );
    }

    public void validateDriverNotAssignedToTruck(
            DriverEntity driverEntity,
            TruckEntity truckEntity
    ) {
        if (driverEntity.getCurrentTruck() != null && driverEntity.getCurrentTruck().getId().equals(truckEntity.getId())) {
            throw new IllegalArgumentException(
                    "Driver already assigned to truck with id=%s"
                            .formatted(truckEntity.getId()));
        }
    }

    public void validateTruckBelongsToDriverCity(
            DriverEntity driverEntity,
            TruckEntity truckEntity
    ) {
        if (!driverEntity.getCurrentCity().getId().equals(truckEntity.getCurrentCity().getId())) {
            throw new IllegalArgumentException(
                    "Truck does not belong to the current city, truck id=%s"
                            .formatted(driverEntity.getCurrentCity().getId())
            );
        }
    }

    public void validateTruckHasAvailableSeats(
            TruckEntity truckEntity
    ) {

        if(truckEntity.getDrivers() == null){
            log.warn("Drivers collection is null for truck id={}",truckEntity.getId());
            throw new IllegalArgumentException("Truck data is incomplete");
        }

        log.info("Truck entity drivers size before validation: {}", truckEntity.getDrivers().size());
        log.info("Default truck seats before validation: {}", defaultTruckSeats);

        if (truckEntity.getDrivers().size() >= getDefaultTruckSeats()) {
            throw new IllegalArgumentException(
                    "There are no seats in the current truck with id=%s"
                            .formatted(truckEntity.getId())
            );
        }
    }

}

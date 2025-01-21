package org.example.logisticapplication.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DriverValidHelper {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private static final int DEFAULT_TRUCK_SEATS = 2;
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
        if (truckEntity.getDrivers().size() >= DEFAULT_TRUCK_SEATS) {
            throw new IllegalArgumentException(
                    "There are no seats in the current truck with id=%s"
                            .formatted(truckEntity.getId())
            );
        }
    }

}

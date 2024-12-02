package org.example.logisticapplication.utils;

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

    @Transactional(readOnly = true)
    public TruckEntity findTruckById(Long truckId) {
        return truckRepository.findById(truckId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Truck does not exist with id=%s"
                                .formatted(truckId)
                )
        );
    }

    @Transactional(readOnly = true)
    public DriverEntity findDriverById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Driver does not exist with id=%s"
                                .formatted(driverId)
                )
        );
    }

    public void validateDriverNotAssignedToTruck(DriverEntity driverEntity, TruckEntity truckEntity) {
        if (driverEntity.getCurrentTruck() != null && driverEntity.getCurrentTruck().getId().equals(truckEntity.getId())) {
            throw new IllegalArgumentException(
                    "Driver already assigned to truck with id=%s"
                            .formatted(truckEntity.getId()));
        }
    }

    public void validateTruckBelongsToDriverCity(DriverEntity driverEntity, TruckEntity truckEntity) {
        if (!driverEntity.getCurrentCity().getId().equals(truckEntity.getCurrentCity().getId())) {
            throw new IllegalArgumentException(
                    "Truck does not belong to the current city, truck id=%s"
                            .formatted(driverEntity.getCurrentCity().getId())
            );
        }
    }

    public void validateTruckHasAvailableSeats(Long truckId, TruckEntity truckEntity) {
        if (truckEntity.getDrivers().size() >= 2) {
            throw new IllegalArgumentException(
                    "There are no seats in the current truck with id=%s"
                            .formatted(truckId)
            );
        }
    }
}

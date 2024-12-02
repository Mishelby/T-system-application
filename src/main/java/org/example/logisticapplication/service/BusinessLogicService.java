package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverEntityConverter;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.utils.DriverMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BusinessLogicService implements DriverLogicService {
    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final DriverMapper driverMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Driver addTruckForDriver(
            Long driverId,
            Long truckId
    ) {
        var driverEntity = findDriverById(driverId);
        var truckEntity = findTruckById(truckId);


        validateDriverNotAssignedToTruck(driverEntity, truckEntity);
        validateTruckBelongsToDriverCity(driverEntity, truckEntity);
        validateTruckHasAvailableSeats(truckId, truckEntity);

        truckEntity.getDrivers().add(driverEntity);
        driverEntity.setCurrentTruck(truckEntity);

        driverRepository.save(driverEntity);

        return driverMapper.toDomain(driverEntity);
    }

    private TruckEntity findTruckById(Long truckId) {
        return truckRepository.findById(truckId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Truck does not exist with id=%s"
                                .formatted(truckId)
                )
        );
    }

    private DriverEntity findDriverById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Driver does not exist with id=%s"
                                .formatted(driverId)
                )
        );
    }

    private void validateDriverNotAssignedToTruck(DriverEntity driverEntity, TruckEntity truckEntity) {
        if (driverEntity.getCurrentTruck() != null && driverEntity.getCurrentTruck().getId().equals(truckEntity.getId())) {
            throw new IllegalArgumentException(
                    "Driver already assigned to truck with id=%s"
                            .formatted(truckEntity.getId()));
        }
    }

    private void validateTruckBelongsToDriverCity(DriverEntity driverEntity, TruckEntity truckEntity) {
        if (!driverEntity.getCurrentCity().getId().equals(truckEntity.getCurrentCity().getId())) {
            throw new IllegalArgumentException(
                    "Truck does not belong to the current city, truck id=%s"
                            .formatted(driverEntity.getCurrentCity().getId())
            );
        }
    }

    private void validateTruckHasAvailableSeats(Long truckId, TruckEntity truckEntity) {
        if (truckEntity.getDrivers().size() >= 2) {
            throw new IllegalArgumentException(
                    "There are no seats in the current truck with id=%s"
                            .formatted(truckId)
            );
        }
    }

}

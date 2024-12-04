package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.utils.DriverMapper;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessLogicService implements DriverLogicService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverValidHelper driverValidHelper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
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

}

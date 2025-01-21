package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.utils.BusinessLogicHelper;
import org.example.logisticapplication.utils.DriverMapper;
import org.example.logisticapplication.utils.DriverValidHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BusinessLogicService implements DriverLogicService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final DriverValidHelper driverValidHelper;

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
        BusinessLogicHelper.isValidStatus(status);
        driverRepository.changeShiftForDriverById(driverId, status);
    }


    @Transactional
    public void changeDriverStatus(
            Long driverId,
            String status
    ) {
        /**
         * Check correct status for driver {correct: DRIVING, THE_SECOND_DRIVER, LOADING_UNLOADING_OPERATIONS}
         */
        BusinessLogicHelper.isValidStatus(status);
        driverRepository.changeDriverStatusById(driverId, status);
    }

}

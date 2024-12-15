package org.example.logisticapplication.service;

import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Truck.Truck;

public interface DriverLogicService {

    Driver addTruckForDriver(Long driverId, Long truck);

    void changeShiftStatus(Long driverId, String status);

    void changeDriverStatus(Long driverId, String status);
}

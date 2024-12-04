package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.service.BusinessLogicService;
import org.example.logisticapplication.utils.DriverMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverLogisticController {
    private final BusinessLogicService businessLogicService;
    private final DriverMapper driverMapper;

    @PutMapping("/{driverId}/add-truck/{truckId}")
    public ResponseEntity<DriverDto> addTruck(
            @PathVariable("driverId") Long driverId,
            @PathVariable("truckId") Long truckId
    ) {
        log.info("Get request for adding truck {} to driver {}", truckId, driverId);
        var driver = businessLogicService.addTruckForDriver(driverId, truckId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        driverMapper.toDto(driver)
                );
    }
}

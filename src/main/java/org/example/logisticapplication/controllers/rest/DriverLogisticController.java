package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.service.BusinessLogicService;
import org.example.logisticapplication.mapper.DriverMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
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
        log.info("Put request for adding truck {} to driver {}", truckId, driverId);
        var driver = businessLogicService.addTruckForDriver(driverId, truckId);

        return ResponseEntity.ok(driverMapper.toDto(driver));
    }


    @PutMapping("/{id}/shift-status")
    public ResponseEntity<Void> updateWorkShiftStatus(
            @PathVariable("id") Long driverId,
            @RequestParam(value = "status") ShiftStatus status
    ) {
        log.info("Put request for work-shift to driver {}", driverId);
        businessLogicService.changeShiftStatus(driverId, status);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<HttpStatus> updateDriverStatus(
            @PathVariable("id") Long driverId,
            @RequestParam(value = "status") DriverStatus status
    ) {
        log.info("Put request for driver status to driver {}", driverId);
        businessLogicService.changeDriverStatus(driverId, status);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}

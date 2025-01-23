package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Driver.DriverDto;
import org.example.logisticapplication.domain.Driver.DriverInfoDto;
import org.example.logisticapplication.domain.Driver.DriverRegistrationDto;
import org.example.logisticapplication.service.DriverService;
import org.example.logisticapplication.mapper.DriverMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {
    private final DriverService driverService;
    private final DriverMapper driverMapper;

    @GetMapping
    public ResponseEntity<List<DriverDto>> findAll(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "cityName", required = false) String cityName
    ) {
        log.info("Get request for find all drivers");
        var allDrivers = driverService.findAll(status, cityName);

        return ResponseEntity.ok(
                allDrivers.stream()
                        .map(driverMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/get-info/{orderId}")
    public ResponseEntity<DriverInfoDto> findById(
            @PathVariable("orderId") Long orderId

    ) {
        log.info("Get request for get info for driver by id");

        return ResponseEntity.ok(driverService.getInfoForDriver(orderId));
    }

    @PostMapping
    public ResponseEntity<DriverDto> createDriver(
            @RequestBody DriverRegistrationDto driver
    ) {
        log.info("Get request for save driver driver: {}", driver);
        var savedDriver = driverService.createDriver(driver);

        return ResponseEntity.ok(driverMapper.toDto(savedDriver));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> updateDriver(
            @PathVariable("id") Long id,
            @RequestBody DriverDto driverDto
    ) {
        log.info("Get request for update driver driver: {}", driverDto);
        var updatedDriver = driverService.updateDriver(
                id,
                driverMapper.toDomain(driverDto)
        );

        return ResponseEntity.ok(driverMapper.toDto(updatedDriver));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(
            @PathVariable Long id
    ) {
        log.info("Get request for get driver by id: {}", id);
        var foundedDriver = driverService.findById(id);

        return ResponseEntity.ok(driverMapper.toDto(foundedDriver));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriverById(
            @PathVariable Long id
    ) {
        log.info("Delete request for delete driver by id: {}", id);
        driverService.deleteDriver(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

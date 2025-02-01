package org.example.logisticapplication.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Truck.CreateTruckDto;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.domain.Truck.TruckForDriverDto;
import org.example.logisticapplication.service.TruckService;
import org.example.logisticapplication.mapper.TruckMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
@Slf4j
public class TruckController {
    private final TruckService truckService;
    private final TruckMapper truckMapper;

    @PostMapping
    public ResponseEntity<TruckDto> createTruck(
            @Valid @RequestBody CreateTruckDto truck
    ) {
        log.info("Get request for creating truck: {}", truck);
        var savedTruck = truckService.createNewTruck(truck);

        return ResponseEntity.ok(truckMapper.toDto(savedTruck));
    }

    @GetMapping
    public ResponseEntity<List<TruckDto>> getAllTrucks() {
        log.info("Get request for getting all trucks");
        var allTrucks = truckService.findAll();

        var dtoTrucks = allTrucks.stream()
                .map(truckMapper::toDto)
                .toList();


        return ResponseEntity.ok(dtoTrucks);
    }

    @GetMapping(value = "/for-driver")
    public ResponseEntity<List<TruckForDriverDto>> findTrucksForDriver(
            @RequestParam("driverId") Long driverId
    ){
        log.info("Get request for find trucks for driver");
        var truckForDriver = truckService.findTruckForDriver(driverId);

        return ResponseEntity.ok()
                .body(truckForDriver);
    }

    @GetMapping("/free-trucks")
    public ResponseEntity<List<TruckDto>> findFreeTrucks(
            @RequestParam("cityId") Long cityId
    ) {
        log.info("Get request for free trucks");
        var freeTrucks = truckService.findFreeTrucks(cityId);

        return ResponseEntity.ok(
                freeTrucks.stream()
                        .map(truckMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TruckDto> getTruckById(
            @PathVariable Long id
    ) {
        log.info("Get request for getting truck by id: {}", id);
        var truck = truckService.findById(id);
        log.info("Found truck: {}", truck);

        return ResponseEntity.ok(truckMapper.toDto(truck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TruckDto> updateTruck(
            @PathVariable("id") Long id,
            @Valid @RequestBody TruckDto truckDto
    ) {
        log.info("Update request for getting truck by id: {}", id);
        var updateTruck = truckService.updateTruck(
                id,
                truckMapper.toDomain(truckDto)
        );

        return ResponseEntity.ok(truckMapper.toDto(updateTruck));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(
            @PathVariable("id") Long id
    ) {
        log.info("Delete request for deleting truck: {}", id);
        truckService.deleteTruck(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

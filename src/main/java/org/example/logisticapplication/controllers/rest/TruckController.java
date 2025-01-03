package org.example.logisticapplication.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Truck.Truck;
import org.example.logisticapplication.domain.Truck.TruckDto;
import org.example.logisticapplication.service.TruckService;
import org.example.logisticapplication.utils.TruckMapper;
import org.springframework.http.HttpStatus;
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
            @Valid @RequestBody Truck truck
    ) {
        log.info("Get request for creating truck: {}", truck);
        var savedTruck = truckService.createNewTruck(truck);

        return ResponseEntity.ok(truckMapper.toDto(savedTruck));
    }

    @GetMapping
    public ResponseEntity<List<TruckDto>> getAllTrucks() {
        log.info("Get request for getting all trucks");
        var allTrucks = truckService.findAll();

        return ResponseEntity.ok(
                allTrucks.stream()
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

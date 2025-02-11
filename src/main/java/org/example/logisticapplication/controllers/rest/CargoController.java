package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.domain.Cargo.CargoDto;
import org.example.logisticapplication.domain.Cargo.CargoInfoForStatus;
import org.example.logisticapplication.domain.Cargo.CargoStatusDto;
import org.example.logisticapplication.service.CargoService;
import org.example.logisticapplication.mapper.CargoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargos")
@RequiredArgsConstructor
@Slf4j
public class CargoController {
    private final CargoService cargoService;
    private final CargoMapper cargoMapper;

    @PostMapping
    public ResponseEntity<CargoDto> addCargo(
            @RequestBody Cargo cargo
    ) {
        log.info("Get request for add cargo: {}", cargo);
        var newCargo = cargoService.createNewCargo(cargo);

        return ResponseEntity.ok().body(cargoMapper.toDto(newCargo));
    }

    @GetMapping("/by-order")
    public ResponseEntity<List<CargoInfoForStatus>> getCargosForOrder(
            @RequestBody String orderNumber
    ) {
        log.info("Get request for get cargos {}", orderNumber);
        var cargosForOrder = cargoService.findCargosForOrder(orderNumber);
        log.info("Cargo info! {}", cargosForOrder);

        return ResponseEntity.ok().body(cargosForOrder);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<List<CargoStatusDto>> getCargoStatus(
            @PathVariable Long id
    ) {
        log.info("Get request for get cargo status: {}", id);
        var cargoStatus = cargoService.getCargoStatus(id);

        return ResponseEntity.ok(cargoStatus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDto> getCargoById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for get cargo: {}", id);
        var newCargo = cargoService.findCargoById(id);

        return ResponseEntity.ok().body(cargoMapper.toDto(newCargo));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateCargoStatus(
            @PathVariable Long id,
            @RequestBody CargoStatusDto cargoStatus
    ){
        log.info("Get request for update cargo status: {}", cargoStatus);
        var httpStatus = cargoService.updateCargoStatus(cargoStatus);

        return ResponseEntity.status(httpStatus).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(
            @PathVariable("id") Long id
    ) {
        log.info("Delete request for get cargo: {}", id);
        cargoService.deleteCargo(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

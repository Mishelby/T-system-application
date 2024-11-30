package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.domain.Cargo.CargoDto;
import org.example.logisticapplication.service.CargoService;
import org.example.logisticapplication.utils.CargoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cargos")
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        cargoMapper.toDto(newCargo)
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDto> getCargoById(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for get cargo: {}", id);
        var newCargo = cargoService.findCargoById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        cargoMapper.toDto(newCargo)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(
            @PathVariable("id") Long id
    ){
        log.info("Delete request for get cargo: {}", id);
        cargoService.deleteCargo(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityDto;
import org.example.logisticapplication.domain.City.CityDtoConverter;
import org.example.logisticapplication.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
@Slf4j
public class CityController {
    private final CityService cityService;
    private final CityDtoConverter cityDtoConverter;

    @PostMapping
    public ResponseEntity<CityDto> addCity(@RequestBody City city) {
        log.info("Get request for add city: {}", city);
        var newCity = cityService.addNewCity(city);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cityDtoConverter.toDto(newCity)
                );
    }

    @GetMapping
    public ResponseEntity<List<CityDto>> getAllCities() {
        log.info("Get request for get cities");
        var allCities = cityService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allCities
                        .stream()
                        .map(cityDtoConverter::toDto)
                        .toList()
                );
    }
}

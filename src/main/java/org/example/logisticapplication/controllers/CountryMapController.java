package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapDto;
import org.example.logisticapplication.domain.Distance.Distance;
import org.example.logisticapplication.domain.Distance.DistanceDto;
import org.example.logisticapplication.service.CountryMapService;
import org.example.logisticapplication.utils.CountryMapMapper;
import org.example.logisticapplication.utils.DistanceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Slf4j
public class CountryMapController {
    private final CountryMapService countryMapService;
    private final CountryMapMapper countryMapMapper;
    private final DistanceMapper distanceMapper;

    @PostMapping
    public ResponseEntity<CountryMapDto> addCountryMap(
            @RequestBody final CountryMap countryMap
    ) {
        log.info("Get request for adding CountryMap: {}", countryMap);
        var newCountryMap = countryMapService.addNewCountryMap(countryMap);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        countryMapMapper.toDto(newCountryMap)
                );
    }

    @PostMapping("/{id}/add-distance")
    public ResponseEntity<DistanceDto> addDistance(
            @RequestBody final Distance distance
    ) {
        log.info("Get request for adding Distance: {}", distance);
        var newDistance = countryMapService.addDistances(distance);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        distanceMapper.toDto(newDistance)
                );
    }

    @PutMapping("/{id}/add-city/{cityId}")
    public ResponseEntity<Void> addCityToCountryMap(
            @PathVariable("id") Long countryId,
            @PathVariable("cityId") Long cityId

    ) {
        log.info("Get request for adding CityToCountryMap: {}", countryId);
        countryMapService.addNewCity(countryId, cityId);

        return ResponseEntity
                .ok()
                .build();
    }
}

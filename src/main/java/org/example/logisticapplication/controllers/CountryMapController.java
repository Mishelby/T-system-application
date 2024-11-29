package org.example.logisticapplication.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapDto;
import org.example.logisticapplication.domain.CountryMap.CountryMapDtoConverter;
import org.example.logisticapplication.domain.Distance.DistanceDto;
import org.example.logisticapplication.service.CountryMapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Slf4j
public class CountryMapController {
    private final CountryMapService countryMapService;
    private final CountryMapDtoConverter countryMapDtoConverter;

    @PostMapping
    public ResponseEntity<CountryMapDto> addCountryMap(
            @RequestBody final CountryMap countryMap
    ) {
        log.info("Get request for adding CountryMap: {}", countryMap);

        var newCountryMap = countryMapService.addNewCountryMap(countryMap);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        countryMapDtoConverter.toDto(newCountryMap)
                );
    }

    @PostMapping("/{id}/add-distance")
    public ResponseEntity<DistanceDto> addDistance(
            @PathVariable("id") final Long id,
            @RequestBody final DistanceDto distanceDto
    ) {
        countryMapService.addDistances(id, distanceDto);
    }

    @PutMapping("/{id}/add-city/{cityId}")
    public ResponseEntity<Void> addCityToCountryMap(
            @PathVariable("id") Long countryId,
            @PathVariable("cityId") Long cityId

    ) {
        countryMapService.addNewCity(countryId, cityId);
        return ResponseEntity
                .ok()
                .build();
    }
}

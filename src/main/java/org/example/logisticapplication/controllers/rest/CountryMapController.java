package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapDto;
import org.example.logisticapplication.domain.Distance.Distance;
import org.example.logisticapplication.domain.Distance.DistanceDto;
import org.example.logisticapplication.service.CountryMapService;
import org.example.logisticapplication.mapper.CountryMapMapper;
import org.example.logisticapplication.mapper.DistanceMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
@Slf4j
public class CountryMapController {
    private final CountryMapService countryMapService;
    private final CountryMapMapper countryMapMapper;
    private final DistanceMapper distanceMapper;

    @GetMapping
    public ResponseEntity<List<CountryMapDto>> findAll() {
        log.info("Get request for all CountryMaps");
        var countryMaps = countryMapService.findAll();

        return ResponseEntity.ok(
                countryMaps.stream()
                        .map(countryMapMapper::toDto)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<CountryMapDto> createCountryMap(
            @RequestBody CountryMap countryMap
    ) {
        log.info("Get request for adding CountryMap: {}", countryMap);
        var newCountryMap = countryMapService.createNewCountryMap(countryMap);

        return ResponseEntity.ok(countryMapMapper.toDto(newCountryMap));
    }

    @PostMapping("/{id}/add-distance")
    public ResponseEntity<DistanceDto> addDistance(
            @RequestBody Distance distance,
            @PathVariable("id") Long countryId
    ) {
        log.info("Get request for adding Distance: {}", distance);
        var newDistance = countryMapService.addDistances(distance, countryId);

        return ResponseEntity.ok(distanceMapper.toDto(newDistance));
    }

}

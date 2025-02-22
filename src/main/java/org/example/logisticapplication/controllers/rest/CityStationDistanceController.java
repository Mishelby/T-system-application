package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.CityStationDistance.CityStationDistanceDto;
import org.example.logisticapplication.service.CityStationDistanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/v1/stations-distances")
@RequiredArgsConstructor
public class CityStationDistanceController {
    private final CityStationDistanceService cityStationDistanceService;

    @PostMapping
    public ResponseEntity<Void> addDistanceForCityStations(
            @RequestBody CityStationDistanceDto stationDistanceDto
    ) {
        log.info("Post request for add distance for city stations {}", stationDistanceDto);
        var stationIds = cityStationDistanceService.addDistanceForStations(stationDistanceDto);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .queryParam("ids", stationIds)
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }
}

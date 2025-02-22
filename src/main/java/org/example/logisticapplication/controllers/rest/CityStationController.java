package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.CityStationEntity.CreateStationDto;
import org.example.logisticapplication.domain.CityStationEntity.CreatedStationDto;
import org.example.logisticapplication.service.CityStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class CityStationController {
    private final CityStationService cityStationService;

    @PostMapping
    public ResponseEntity<CreatedStationDto> createNewStation(
            @RequestBody CreateStationDto stationDto
    ){
        log.info("Get request for creating new station: {}", stationDto);
        var station = cityStationService.createStation(stationDto);

        return ResponseEntity.ok().body(station);
    }

    @GetMapping
    public ResponseEntity<List<CreatedStationDto>> getAllStations() {
        log.info("Get request for getting all stations");
        var allStations = cityStationService.getAllStations();

        return ResponseEntity.ok().body(allStations);
    }


}

package org.example.logisticapplication.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityDto;
import org.example.logisticapplication.domain.City.CityWithStationsDto;
import org.example.logisticapplication.service.CityService;
import org.example.logisticapplication.mapper.CityMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Slf4j
public class CityController {
    private final CityService cityService;
    private final CityMapper cityMapper;

    @PostMapping
    public ResponseEntity<CityDto> createCity(
            @RequestBody City city
    ) {
        log.info("Get request for add city: {}", city);
        var newCity = cityService.addNewCity(city);

        return ResponseEntity.ok(cityMapper.toDto(newCity));
    }

    @GetMapping
    public ResponseEntity<List<CityDto>> getAllCities() {
        log.info("Get request for all cities");
        var allCities = cityService.findAllCities();

        return ResponseEntity.ok(allCities.stream()
                .map(cityMapper::toDto)
                .toList());
    }

    @GetMapping("/stations")
    public ResponseEntity<CityWithStationsDto> getCityStationsByName(
            @RequestParam String cityName
    ){
        log.info("Get request for get cities by name");
        var cityWithStations = cityService.findCityWithStations(cityName);

        return ResponseEntity.ok().body(cityWithStations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityDto> getCity(
            @PathVariable("id") Long id
    ) {
        log.info("Get request for get city: {}", id);
        var city = cityService.findById(id);

        return ResponseEntity.ok(cityMapper.toDto(city));
    }

    @GetMapping("/by-country")
    public ResponseEntity<List<CityDto>> getAllCities(
            @RequestParam(value = "countryId", required = false)
            Long countryId
    ) {
        log.info("Get request for get cities");
        var allCities = cityService.findAllCitiesByCountryId(countryId);

        return ResponseEntity.ok(
                allCities.stream()
                        .map(cityMapper::toDto)
                        .toList());
    }

}

package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.City.CityWithStationsDto;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.mapper.CityMapper;
import org.example.logisticapplication.web.CityNotFoundException;
import org.example.logisticapplication.web.CountryMapNotFoundException;
import org.example.logisticapplication.web.EntityAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CountryMapRepository countryMapRepository;

    @Transactional(readOnly = true)
    public List<City> findAllCitiesByCountryId(
            Long countryId
    ) {
        var allCities = cityRepository.findAllByCountryMapId(countryId);

        if (allCities.isEmpty()) {
            return new ArrayList<>();
        }

        return allCities
                .stream()
                .map(cityMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CityWithStationsDto> findAllCitiesWithStations(
            String countryName
    ) {
        return cityRepository.findAllCitiesByCountryName(countryName)
                .stream()
                .map(cityMapper::toWithStationsDto)
                .toList();
    }

    @Transactional
    public City addNewCity(
            City city
    ) {
        validateCityDoesNotExist(city.name());
        var countryMapEntity = getCountryMapEntity(city);

        var savedCity = cityRepository.save(
                cityMapper.toEntity(city, countryMapEntity)
        );

        return cityMapper.toDomain(savedCity);
    }

    private CountryMapEntity getCountryMapEntity(City city) {
        return countryMapRepository.findByCountryName(city.countryMapName())
                .orElseThrow(
                        () -> countryMapNotFound(city.countryMapName())
                );

    }

    @Transactional(readOnly = true)
    public City findById(
            Long id
    ) {
        var cityEntity = cityRepository.findById(id).orElseThrow(
                () -> cityNotFound(id)
        );

        return cityMapper.toDomain(cityEntity);
    }

    @Transactional(readOnly = true)
    public List<City> findAllCities() {
        var allCities = cityRepository.findAll();

        if (allCities.isEmpty()) {
            return new ArrayList<>();
        }

        return allCities.stream()
                .map(cityMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public CityWithStationsDto findCityWithStations(
            String cityName
    ) {
        var cityEntity = cityRepository.findCityWithStations(cityName).orElseThrow();
        var stationsNames = cityEntity.getCityStation().stream()
                .map(CityStationEntity::getName)
                .toList();

        return new CityWithStationsDto(
                cityEntity.getName(),
                stationsNames.isEmpty()
                        ? Collections.emptyList()
                        : stationsNames
        );
    }

    public void validateCityDoesNotExist(
            String name
    ) {
        if (cityRepository.existsCityEntityByName(name)) {
            throw new EntityAlreadyExistsException("City", "name", name);
        }
    }

    private CountryMapNotFoundException countryMapNotFound(
            String name
    ) {
        return new CountryMapNotFoundException("Country Map with name = %s not found"
                .formatted(name));
    }

    private <T> CityNotFoundException cityNotFound(
            T param
    ) {
        return new CityNotFoundException("City with param = %s not found"
                .formatted(param));
    }

}

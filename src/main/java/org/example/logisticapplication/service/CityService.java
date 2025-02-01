package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.mapper.CityMapper;
import org.example.logisticapplication.web.CityNotFoundException;
import org.example.logisticapplication.web.CountryMapNotFoundException;
import org.example.logisticapplication.web.EntityAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public City addNewCity(
            City city
    ) {
        validateCityDoesNotExist(city.name());

        var countryMapEntity = countryMapRepository.findByCountryName(city.countryMapName())
                .orElseThrow(
                        () -> countryMapNotFound(city.countryMapName())
                );

        var savedCity = cityRepository.save(
                cityMapper.toEntity(city, countryMapEntity)
        );


        return cityMapper.toDomain(savedCity);
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

    public void validateCityDoesNotExist(
            String name
    ) {
        if (cityRepository.existsCityEntityByName(name)) {
            throw new EntityAlreadyExistsException("City", "name", name);
        }
    }

    private EntityNotFoundException entityNotFound(
            String entityName,
            Long id
    ) {
        return new EntityNotFoundException("%s with id=%s does not exist!"
                .formatted(entityName, id));
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

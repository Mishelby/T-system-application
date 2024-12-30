package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.utils.CityMapper;
import org.example.logisticapplication.web.EntityAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CountryMapRepository countryMapRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public City addNewCity(
            City city
    ) {
        validateCityDoesNotExist(city.name());

        var countryMapEntity = countryMapRepository.findById(city.countryMapId()).orElseThrow(
                () -> entityNotFound("Country Map", city.countryMapId())
        );

        var savedCity = cityRepository.save(
                cityMapper.toEntity(city, countryMapEntity)
        );


        return cityMapper.toDomain(savedCity);
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

    @Transactional(readOnly = true)
    public City findById(
            Long id
    ) {
        var cityEntity = cityRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        "City with id=%s not found!"
                                .formatted(id)
                )
        );

        return cityMapper.toDomain(cityEntity);
    }

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
}

package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.utils.CountryMapMapper;
import org.example.logisticapplication.utils.DistanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryMapService {
    private final CountryMapRepository countryMapRepository;
    private final CountryMapMapper countryMapMapper;
    private final CityRepository cityRepository;
    private final DistanceMapper distanceMapper;


    @Transactional(readOnly = true)
    public List<CountryMap> findAll() {
        var allCountryMaps = countryMapRepository.findAllMaps();

        if (allCountryMaps.isEmpty()) {
            return Collections.emptyList();
        }

        return allCountryMaps.stream()
                .map(countryMapMapper::toDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public CountryMap findById(Long id) {
        return countryMapMapper.toDomain(
                countryMapRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Country map with id=%s does not exist"
                                        .formatted(id)
                        )
                )
        );
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CountryMap createNewCountryMap(
            CountryMap countryMap
    ) {
        isCountryMapAlreadyExists(countryMap);

        var entity = countryMapRepository.save(
                countryMapMapper.toEntity(countryMap)
        );
        return countryMapMapper.toDomain(entity);
    }

    private void isCountryMapAlreadyExists(CountryMap countryMap) {
        if (countryMapRepository.existsCountryMapEntitiesByCountryName(countryMap.name())) {
            throw new IllegalArgumentException(
                    "Country with name=%s already exists"
                            .formatted(countryMap.name())
            );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public Distance addDistances(
            Distance distance,
            Long countryId
    ) {
        var countryMapEntity = findCountryMap(countryId);
        var cityIds = List.of(distance.fromCityId(), distance.toCityId());
        var citiesByIds = findAndValidateCities(cityIds, countryId);


        var cityFrom = citiesByIds.stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.fromCityId()))
                .findFirst()
                .orElseThrow();


        var toCity = citiesByIds.stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.toCityId()))
                .findFirst()
                .orElseThrow();

        isDistanceAlreadyExists(cityFrom, toCity);

        var distanceEntity = distanceMapper.toEntity(
                distance,
                cityFrom,
                toCity,
                countryMapEntity
        );

        distanceEntity.setCountryMap(countryMapEntity);
        countryMapEntity.getDistances().add(distanceEntity);
        countryMapRepository.save(countryMapEntity);

        return distanceMapper.toDomain(distanceEntity);
    }

    private void isDistanceAlreadyExists(CityEntity cityFrom, CityEntity toCity) {
        if (cityRepository.existsByCities(cityFrom, toCity)) {
            throw new IllegalArgumentException(
                    "Distance between these cities already exists, cities: %s and %s"
                            .formatted(cityFrom.getName(), toCity.getName())
            );
        }
    }

    private List<CityEntity> findAndValidateCities(List<Long> cityIds, Long countryId) {
        var foundCities = cityRepository.findCitiesByIds(cityIds, countryId);

        if (!cityIds.equals(foundCities.stream().map(CityEntity::getId).toList())) {
            throw new IllegalArgumentException(
                    "One or both cities not found in country map with id=%s. Expected: %s, found: %s"
                            .formatted(
                                    countryId,
                                    cityIds,
                                    foundCities.stream().map(CityEntity::getId).toList()
                            )
            );
        }

        return cityIds.stream()
                .map(id -> foundCities.stream()
                        .filter(city -> city.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("City with id=%s not found".formatted(id))))
                .toList();
    }

    private CountryMapEntity findCountryMap(Long countryId) {
        return countryMapRepository.findById(countryId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Country map with id=%s does not exist"
                                .formatted(countryId)
                )
        );
    }


}

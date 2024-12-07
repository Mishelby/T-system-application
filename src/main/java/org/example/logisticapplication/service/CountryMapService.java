package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.Distance.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.utils.CountryMapMapper;
import org.example.logisticapplication.utils.DistanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryMapService {
    private final CountryMapRepository countryMapRepository;
    private final CountryMapMapper countryMapMapper;
    private final CityRepository cityRepository;
    private final DistanceMapper distanceMapper;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CountryMap addNewCountryMap(
            CountryMap countryMap
    ) {
        if (countryMapRepository.existsCountryMapEntitiesByCountryName(countryMap.countryName())) {
            throw new IllegalArgumentException(
                    "Country with name=%s already exists"
                            .formatted(countryMap.countryName())
            );
        }

        var entity = countryMapRepository.save(
                countryMapMapper.toEntity(countryMap)
        );

        return countryMapMapper.toDomain(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void addNewCity(
            Long countryId,
            Long cityId
    ) {
        if (!countryMapRepository.existsCountryMapEntitiesById(countryId)) {
            throw new EntityNotFoundException(
                    "Country map with id=%s does not exist"
                            .formatted(countryId)
            );
        }

        var cityEntity = cityRepository.findById(cityId).orElseThrow(
                () -> new EntityNotFoundException(
                        "City with id=%s does not exist"
                                .formatted(cityId)
                )
        );

        var countryMapEntity = countryMapRepository.findById(countryId).orElseThrow();

        if (!countryMapEntity.getCities().contains(cityEntity)) {
            countryMapEntity.getCities().add(cityEntity);
            countryMapRepository.save(countryMapEntity);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public Distance addDistances(
            Long countryMapId,
            Distance distance
    ) {
        if (!countryMapRepository.existsCountryMapEntitiesById(countryMapId)) {
            throw new EntityNotFoundException(
                    "Country map with id=%s does not exist"
                            .formatted(countryMapId)
            );
        }

        var countryMapEntity = countryMapRepository.findById(countryMapId).orElseThrow();

        var citiesByIds = cityRepository.findCitiesByIds(List.of(
                        distance.fromCityId(),
                        distance.toCityId()
                ),
                distance.countryMapId());

        if (citiesByIds.size() != 2) {
            throw new IllegalArgumentException("One or both cities not found");
        }

        var cityFrom = citiesByIds
                .stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.fromCityId()))
                .findFirst()
                .orElseThrow();

        var cityTo = citiesByIds
                .stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.toCityId()))
                .findFirst()
                .orElseThrow();

        if (cityRepository.existsByCities(cityFrom, cityTo)) {
            throw new IllegalArgumentException(
                    "Distance between these cities already exists, cities: %s and %s"
                            .formatted(cityFrom.getName(), cityTo.getName())
            );
        }

        var distanceEntity = distanceMapper.toEntity(
                distance,
                cityFrom,
                cityTo,
                countryMapEntity
        );

        distanceEntity.setCountryMap(countryMapEntity);
        countryMapEntity.getDistances().add(distanceEntity);
        countryMapRepository.save(countryMapEntity);

        return distanceMapper.toDomain(distanceEntity);
    }
}

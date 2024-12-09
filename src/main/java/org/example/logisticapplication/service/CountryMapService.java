package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
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
            Distance distance
    ) {
        var countryMapEntity = countryMapRepository
                .findById(distance.countryMapId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Country map with id=%s does not exist"
                                        .formatted(distance.countryMapId())
                        )
                );

        var citiesByIds = cityRepository.findCitiesByIds(
                List.of(
                        distance.fromCityId(),
                        distance.toCityId()
                ),
                distance.countryMapId()
        );

        var cityIds = List.of(distance.fromCityId(), distance.toCityId());

        if (!cityIds.equals(citiesByIds.stream().map(CityEntity::getId).toList())) {
            throw new IllegalArgumentException(
                    "One or both cities not found in country map with id=%s. Expected cities: %s, found cities: %s"
                            .formatted(
                                    distance.countryMapId(),
                                    cityIds,
                                    citiesByIds.stream().map(CityEntity::getId).toList()
                            )
            );
        }

        var cityFrom = citiesByIds.stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.fromCityId()))
                .findFirst()
                .orElseThrow();


        var toCity = citiesByIds.stream()
                .filter(cityEntity -> cityEntity.getId().equals(distance.toCityId()))
                .findFirst()
                .orElseThrow();

        if (cityRepository.existsByCities(cityFrom, toCity)) {
            throw new IllegalArgumentException(
                    "Distance between these cities already exists, cities: %s and %s"
                            .formatted(cityFrom.getName(), toCity.getName())
            );
        }

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
}

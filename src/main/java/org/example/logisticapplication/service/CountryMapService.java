package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntityConverter;
import org.example.logisticapplication.domain.Distance.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryMapService {
    private final CountryMapRepository countryMapRepository;
    private final CountryMapEntityConverter countryMapEntityConverter;
    private final CityRepository cityRepository;
    private final DistanceDtoConverter distanceDtoConverter;
    private final DistanceEntityConverter distanceEntityConverter;

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
                countryMapEntityConverter.toEntity(
                        countryMap,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );

        return countryMapEntityConverter.toDomain(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void addNewCity(
            Long countryId,
            Long cityId
    ) {
        if (countryMapRepository.existsCountryMapEntitiesById(countryId)) {
            throw new IllegalArgumentException(
                    "Country map with id=%s does not exist"
                            .formatted(countryId)
            );
        }

        var cityEntity = cityRepository.findById(cityId).orElseThrow(
                () -> new IllegalArgumentException(
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
            DistanceDto distanceDto
    ) {
        if (!countryMapRepository.existsCountryMapEntitiesById(countryMapId)) {
            throw new IllegalArgumentException(
                    "Country map with id=%s does not exist"
                            .formatted(countryMapId)
            );
        }

        var countryMapEntity = countryMapRepository.findById(countryMapId).orElseThrow();

        var citiesByIds = cityRepository.findCitiesByIds(List.of(
                distanceDto.fromCityId(),
                distanceDto.toCityId()
        ));

        if (citiesByIds.size() != 2) {
            throw new IllegalArgumentException("One or both cities not found");
        }

        var cityFrom = citiesByIds
                .stream()
                .filter(cityEntity -> cityEntity.getId().equals(distanceDto.fromCityId()))
                .findFirst()
                .orElseThrow();

        var cityTo = citiesByIds
                .stream()
                .filter(cityEntity -> cityEntity.getId().equals(distanceDto.toCityId()))
                .findFirst()
                .orElseThrow();


        var distanceEntity = distanceEntityConverter.toEntity(
                distanceDtoConverter.toDomain(distanceDto),
                cityFrom,
                cityTo
        );

        countryMapEntity.getDistances().add(distanceEntity);
        countryMapRepository.save(countryMapEntity);

        return distanceEntityConverter.toDomain(distanceEntity);
    }
}

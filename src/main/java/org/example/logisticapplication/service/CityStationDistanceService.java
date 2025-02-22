package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.CityStationDistance.CityStationDistanceDto;
import org.example.logisticapplication.domain.CityStationDistance.CityStationDistanceEntity;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.repository.CityStationDistanceRepository;
import org.example.logisticapplication.repository.CityStationRepository;
import org.example.logisticapplication.repository.CountryMapRepository;
import org.example.logisticapplication.repository.DistanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityStationDistanceService {
    private final CityStationDistanceRepository distanceRepository;
    private final CityStationRepository cityStationRepository;
    private final CountryMapRepository countryMapRepository;

    @Transactional
    public List<Long> addDistanceForStations(
            CityStationDistanceDto stationDistanceDto
    ) {
        isDistanceExists(stationDistanceDto.stationFrom(), stationDistanceDto.stationTo());
        var countryMapEntity = countryMapRepository.findByCountryName(stationDistanceDto.countryMapName()).orElseThrow();

        var stationsByName = cityStationRepository.findStationsByName(
                stationDistanceDto.stationFrom(),
                stationDistanceDto.stationTo()
        ).stream().collect(Collectors.toMap(
                CityStationEntity::getName,
                Function.identity()
        ));

        var stationDistance = new CityStationDistanceEntity(
                stationsByName.get(stationDistanceDto.stationFrom()),
                stationsByName.get(stationDistanceDto.stationTo()),
                stationDistanceDto.distance(),
                countryMapEntity
        );
        distanceRepository.save(stationDistance);

        return stationsByName.values().stream()
                .map(CityStationEntity::getId)
                .toList();
    }

    private void isDistanceExists(String stationFrom, String stationTo) {
        boolean isExists = distanceRepository.isExistsDistanceByStationNames(List.of(stationFrom, stationTo));

        if (isExists) {
            throw new IllegalArgumentException(
                    "Distance between station %s and %s already exists"
                            .formatted(stationFrom, stationTo)
            );
        }
    }
}

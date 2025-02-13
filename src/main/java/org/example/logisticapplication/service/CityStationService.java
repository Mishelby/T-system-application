package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.CityStationEntity.CreateStationDto;
import org.example.logisticapplication.domain.CityStationEntity.CreatedStationDto;
import org.example.logisticapplication.mapper.CityStationMapper;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.CityStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityStationService {
    private final CityStationRepository cityStationRepository;
    private final CityRepository cityRepository;
    private final CityStationMapper cityStationMapper;

    @Transactional
    public CreatedStationDto createStation(
            CreateStationDto stationDto
    ) {
        isExistsByStationName(stationDto.stationName(), stationDto.cityName());

        var cityEntity = cityRepository.findCityEntityByName(stationDto.cityName()).orElseThrow();
        var stationEntity = cityStationMapper.toEntity(stationDto.stationName(), cityEntity);
        cityEntity.getCityStation().add(stationEntity);

        return new CreatedStationDto(
                cityEntity.getId(),
                stationEntity.getName()
        );
    }

    @Transactional(readOnly = true)
    public List<CreatedStationDto> getAllStations() {
        var allStations = cityStationRepository.findAll();

        if (allStations.isEmpty()) {
            return new ArrayList<>();
        }

        return allStations.stream()
                .map(cityStationMapper::toDto)
                .toList();
    }

    private void isExistsByStationName(
            String stationName,
            String cityName
    ) {
        boolean isExists = cityStationRepository.findCityStationEntitiesByName(stationName, cityName);

        if (isExists) {
            throw new IllegalArgumentException("Station with name= %s already exists"
                    .formatted(stationName));
        }
    }
}

package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.utils.CityMapper;
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

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public City addNewCity(
            City city
    ) {
        if (cityRepository.existsCityEntityByName(city.name())) {
            throw new IllegalArgumentException(
                    "City with name=%s already exists!"
                            .formatted(city.name())
            );
        }

        var savedCity = cityRepository.save(
                cityMapper.toEntity(city)
        );


        return cityMapper.toDomain(savedCity);

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
    public List<City> findAll() {
        var allCities = cityRepository.findAll();
        if (allCities.isEmpty()) {
            return new ArrayList<>();
        }

        return allCities
                .stream()
                .map(cityMapper::toDomain)
                .toList();
    }
}

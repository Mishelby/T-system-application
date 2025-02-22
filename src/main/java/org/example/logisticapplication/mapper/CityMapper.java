package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.*;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "countryMap", source = "countryMapEntity"),
            @Mapping(target = "name", source = "city.name"),
    })
    CityEntity toEntity(
            City city,
            CountryMapEntity countryMapEntity
    );

    City toDomain(CityEntity entity);

    CityDto toDto(City city);

    City toDomain(CityDto cityDto);

    @Mappings({
            @Mapping(target = "countyMapName", source = "entity.countryMap.countryName")
    })
    CityInfoDto toInfoDto(CityEntity entity);

    @Mappings({
            @Mapping(target = "cityName", source = "cityEntity.name"),
            @Mapping(target = "stationsNames", expression = "java(stationsNames(cityEntity))"),
    })
    CityWithStationsDto toWithStationsDto(CityEntity cityEntity);

    default List<String> stationsNames(CityEntity city) {
        return city.getCityStation().stream()
                .map(CityStationEntity::getName)
                .toList();
    }

    @AfterMapping
    default void initializeCityStationList(@MappingTarget CityEntity cityEntity) {
        if(cityEntity.getCityStation() == null) {
            cityEntity.setCityStation(new ArrayList<>());
        }
    }
}

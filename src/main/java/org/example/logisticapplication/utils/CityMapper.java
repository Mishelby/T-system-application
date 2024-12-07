package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {

    @Mappings({
            @Mapping(source = "countryMapEntity.id", target = "countryMap.id"),
            @Mapping(source = "city.id", target = "id")
    })
    CityEntity toEntity(City city, CountryMapEntity countryMapEntity);

    City toDomain(CityEntity entity);

    CityDto toDto(City city);

    City toDomain(CityDto cityDto);
}

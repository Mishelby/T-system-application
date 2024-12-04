package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.City.City;
import org.example.logisticapplication.domain.City.CityDto;
import org.example.logisticapplication.domain.City.CityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityMapper {
    CityEntity toEntity(City city);

    City toDomain(CityEntity entity);

    CityDto toDto(City city);

    City toDomain(CityDto cityDto);
}

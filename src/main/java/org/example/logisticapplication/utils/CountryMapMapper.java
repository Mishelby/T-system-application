package org.example.logisticapplication.utils;

import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapDto;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CountryMapMapper {
    CountryMap toDomain(CountryMapDto dto);

    CountryMapDto toDto(CountryMap map);

    @Mappings({
            @Mapping(target = "cities", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "distances", expression = "java(new java.util.ArrayList<>())")
    })
    CountryMapEntity toEntity(CountryMap domain);

    CountryMap toDomain(CountryMapEntity entity);
}

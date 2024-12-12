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
    @Mappings({
            @Mapping(target = "cities", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "distances", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "countryName", source = "domain.name"),
    })
    CountryMapEntity toEntity(CountryMap domain);

    @Mappings({
            @Mapping(target = "name", source = "entity.countryName"),
    })
    CountryMap toDomain(CountryMapEntity entity);

    CountryMap toDomain(CountryMapDto dto);

    CountryMapDto toDto(CountryMap map);
}

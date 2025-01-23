package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMap;
import org.example.logisticapplication.domain.CountryMap.CountryMapDto;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CountryMapMapper {

    @Mappings({
            @Mapping(target = "cities", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "distances", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "countryName", source = "domain.name"),
    })
    CountryMapEntity toEntity(CountryMap domain);

    @Mappings({
            @Mapping(target = "name", source = "entity.countryName"),
            @Mapping(target = "citiesId", source = "entity.cities", qualifiedByName = "toCityIds"),
            @Mapping(target = "distancesId", source = "entity.distances", qualifiedByName = "toDistanceIds")
    })
    CountryMap toDomain(CountryMapEntity entity);

    @Named("toCityIds")
    default Set<Long> toCityIds(Set<CityEntity> cities){
        return cities.stream()
                .map(CityEntity::getId)
                .collect(Collectors.toSet());
    }

    @Named("toDistanceIds")
    default Set<Long> toDistanceIds(Set<DistanceEntity> distances){
        return distances.stream()
                .map(DistanceEntity::getId)
                .collect(Collectors.toSet());
    }

    CountryMap toDomain(CountryMapDto dto);

    CountryMapDto toDto(CountryMap map);
}

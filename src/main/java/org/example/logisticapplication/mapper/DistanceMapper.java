package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.Distance;
import org.example.logisticapplication.domain.Distance.DistanceDto;
import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DistanceMapper {

    @Mappings({
            @Mapping(source = "domain.id", target = "id"),
            @Mapping(source = "domain.fromCityId", target = "fromCity.id"),
            @Mapping(source = "domain.toCityId", target = "toCity.id"),
            @Mapping(source = "domain.distance", target = "distance"),
            @Mapping(source = "countryMap", target = "countryMap")
    })
    DistanceEntity toEntity(
            Distance domain,
            CityEntity from,
            CityEntity to,
            CountryMapEntity countryMap
    );

    Distance toDomain(DistanceEntity entity);

    DistanceDto toDto(Distance domain);

    Distance toDomain(DistanceDto distanceDto);
}

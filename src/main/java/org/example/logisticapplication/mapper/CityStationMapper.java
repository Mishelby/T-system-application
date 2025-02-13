package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CityStationEntity.CreatedStationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CityStationMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name" , source = "name"),
            @Mapping(target = "city" , source = "cityEntity"),
    })
    CityStationEntity toEntity(String name, CityEntity cityEntity);

    @Mappings({
            @Mapping(target = "cityId" , source = "entity.id"),
            @Mapping(target = "stationName" , source = "entity.name")
    })
    CreatedStationDto toDto(CityStationEntity entity);
}

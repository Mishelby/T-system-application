package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.OrderInfo.OrderInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderInfoMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order", source = "orderEntity"),
            @Mapping(target = "cityFrom", source = "cityEntityFrom"),
            @Mapping(target = "cityStationFrom", source = "stationEntityFrom"),
            @Mapping(target = "cityTo", source = "cityEntityTo"),
            @Mapping(target = "cityStationTo", source = "stationEntityTo")
    })
    OrderInfoEntity toEntity(
            final OrderEntity orderEntity,
            final CityEntity cityEntityFrom,
            final CityEntity cityEntityTo,
            final CityStationEntity stationEntityFrom,
            final CityStationEntity stationEntityTo
    );
}

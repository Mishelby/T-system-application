package org.example.logisticapplication.mapper;


import org.example.logisticapplication.domain.Driver.MainDriverInfoDto;
import org.example.logisticapplication.domain.Order.MainOrderInfoDto;
import org.example.logisticapplication.domain.Truck.MainTruckInfoDto;
import org.example.logisticapplication.utils.MainTableInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MainTableMapper {

    @Mappings({
            @Mapping(target = "orderTableInfo", source = "orderInfo"),
            @Mapping(target = "driverInfo", source = "driverInfo"),
            @Mapping(target = "truckInfo", source = "truckInfo"),
            @Mapping(target = "countOfDrivers", source = "countOfDrivers"),
            @Mapping(target = "countOfTrucks", source = "countOfTrucks"),
    })
    MainTableInfoDto toMainTableInfo(
            List<MainOrderInfoDto> orderInfo,
            List<MainDriverInfoDto> driverInfo,
            List<MainTruckInfoDto> truckInfo,
            Long countOfDrivers,
            Long countOfTrucks
    );

}

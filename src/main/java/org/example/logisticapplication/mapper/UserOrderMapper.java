package org.example.logisticapplication.mapper;

import org.example.logisticapplication.domain.Driver.DriverInfoForUserDto;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderStatus;
import org.example.logisticapplication.domain.User.UserOrderInfo;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserOrderMapper {
    @Mappings({
            @Mapping(target = "uniqueNumber", source = "order.uniqueNumber"),
            @Mapping(target = "status", expression = "java(getOrderStatus(order))"),
            @Mapping(target = "message", expression = "java(messageForOrder(order))"),
            @Mapping(target = "orderInfoDto", source = "orderInfoDto"),
    })
    UserOrderInfo toInfoDto(
            OrderEntity order,
            DriverInfoForUserDto orderInfoDto
    );

    @Named("orderStatus")
    default String getOrderStatus(OrderEntity order) {
        return order.getStatus().getStatusName();
    }

    @Condition
    default String messageForOrder(OrderEntity order) {
        return order.getDriverOrders().isEmpty() && order.getTruckOrders().isEmpty()
                ? "Водитель ещё не был назначен!"
                : "Информацию о водителе вы можете увидеть ниже";
    }
}

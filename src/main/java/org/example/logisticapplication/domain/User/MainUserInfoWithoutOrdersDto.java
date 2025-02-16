package org.example.logisticapplication.domain.User;

public record MainUserInfoWithoutOrdersDto(
        UserMainInfoDto userInfoDto,
        UserOrderInfo userOrderInfo
) implements UserInfo {
    @Override
    public boolean getCurrentOrders() {
        return userOrderInfo != null;
    }
}

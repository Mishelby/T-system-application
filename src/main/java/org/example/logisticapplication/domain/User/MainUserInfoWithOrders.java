package org.example.logisticapplication.domain.User;

public record MainUserInfoWithOrders(
        UserMainInfoDto userInfoDto,
        UserOrderInfo userOrderInfo
) implements UserInfo {
    @Override
    public boolean getCurrentOrders() {
        return userOrderInfo != null;
    }
}

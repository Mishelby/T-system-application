package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.OrderProfileInfo.UserProfileInfoEntity;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.example.logisticapplication.repository.*;
import org.example.logisticapplication.utils.ConstantsForMessages;
import org.example.logisticapplication.utils.ConstantsForOrderTime;
import org.example.logisticapplication.utils.DriverInfoForUserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverInfoForUserService {
    private final UserOrderRepository userOrderRepository;
    private final OrderRepository orderRepository;
    private final UserProfileInfoRepository userProfileInfoRepository;

    @EventListener
    @Async
    public void onDriverInfoForUserEvent(DriverInfoForUserEvent event) {
        var orderNumber = event.getOrderNumber();
        var timeForOrder = event.getTime();
        getMessageForUser(orderNumber, timeForOrder);
    }

    protected String getMessageForUser(
            String orderNumber,
            Double time
    ) {
        var messageForUser = new StringBuilder();
        var orderEntity = orderRepository.findOrderEntityByNumber(orderNumber).orElseThrow();

        if (time >= ConstantsForOrderTime.RECOMMENDED_TIME_F0R_ONE_DRIVER.getValue()) {
            messageForUser.append(ConstantsForMessages.DEFAULT_MESSAGE_CHOOSE_THE_NUMBER_OF_DRIVERS.getMessage());
            messageForUser.append(ConstantsForMessages.DEFAULT_MESSAGE_CHOOSE_OR_DECLINE.getMessage());
        }

        var userOrderEntity = userOrderRepository.findByOrderId(orderEntity.getId()).orElseThrow();
        var userProfileInfoEntity = userProfileInfoRepository.getUserProfileInfoById(userOrderEntity.getUser().getId()).orElseThrow();
        saveUserProfileInfo(userProfileInfoEntity, messageForUser);

        return userProfileInfoEntity.getMessageForUser();
    }

    private void saveUserProfileInfo(
            UserProfileInfoEntity userProfileInfoEntity,
            StringBuilder messageForUser
    ) {
        userProfileInfoEntity.setMessageForUser(messageForUser.toString());
        userProfileInfoRepository.save(userProfileInfoEntity);
    }
}

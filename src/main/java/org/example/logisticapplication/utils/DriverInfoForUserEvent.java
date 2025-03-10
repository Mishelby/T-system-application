package org.example.logisticapplication.utils;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DriverInfoForUserEvent extends ApplicationEvent {
    private String orderNumber;
    private Double time;

    public DriverInfoForUserEvent(
            Object source,
            String orderNumber,
            Double time
    ) {
        super(source);
        this.time = time;
        this.orderNumber = orderNumber;
    }
}

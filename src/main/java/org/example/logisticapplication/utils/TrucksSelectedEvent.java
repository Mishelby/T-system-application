package org.example.logisticapplication.utils;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TrucksSelectedEvent extends ApplicationEvent {
    private final String orderNumber;

    public TrucksSelectedEvent(
            Object source,
            String orderNumber
    ) {
        super(source);
        this.orderNumber = orderNumber;
    }
}

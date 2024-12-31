package org.example.logisticapplication.domain.Order;

public enum OrderStatus {
    COMPLETED("COMPLETED"),
    NOT_COMPLETED("NOT COMPLETED");

    final String name;

    OrderStatus(String name) {
        this.name = name;
    }
}

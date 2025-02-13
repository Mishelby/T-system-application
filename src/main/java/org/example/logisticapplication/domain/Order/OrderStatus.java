package org.example.logisticapplication.domain.Order;

public enum OrderStatus {
    COMPLETED("COMPLETED"),
    NOT_COMPLETED("NOT_COMPLETED");

    final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

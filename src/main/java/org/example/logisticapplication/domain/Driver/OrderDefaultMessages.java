package org.example.logisticapplication.domain.Driver;

import lombok.Getter;

@Getter
public enum OrderDefaultMessages {
    ONE_DRIVER_FOR_ORDER("Ваш заказ выполнит один водитель!"),
    MORE_DRIVER_FOR_ORDER("Для ускорения доставки товара, вы можете выбрать несколько водителей!");

    String description;

    OrderDefaultMessages(String name) {
        this.description = name;
    }
}

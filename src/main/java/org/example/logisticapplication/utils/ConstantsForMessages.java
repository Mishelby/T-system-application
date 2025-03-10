package org.example.logisticapplication.utils;

import lombok.Getter;

@Getter
public enum ConstantsForMessages {
    DEFAULT_MESSAGE_CHOOSE_THE_NUMBER_OF_DRIVERS("Для ускорения заказа мы можем подобрать несколько водителей!"),
    DEFAULT_MESSAGE_CHOOSE_OR_DECLINE("Выберите количество водителей, или оставьте выбор нам!");

    private String message;

    ConstantsForMessages(String message) {
        this.message = message;
    }
}

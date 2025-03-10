package org.example.logisticapplication.utils;

import lombok.Getter;

@Getter
public enum ConstantsForOrderTime {
    RECOMMENDED_TIME_F0R_ONE_DRIVER(12.0);

    private Double value;

    ConstantsForOrderTime(Double value) {
        this.value = value;
    }
}

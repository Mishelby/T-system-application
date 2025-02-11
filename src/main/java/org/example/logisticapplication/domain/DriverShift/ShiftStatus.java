package org.example.logisticapplication.domain.DriverShift;

import lombok.Getter;

@Getter
public enum ShiftStatus {
    ON_SHIFT("ON_SHIFT"),
    REST("REST");

    private final String statusName;

    ShiftStatus(String statusName){
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}

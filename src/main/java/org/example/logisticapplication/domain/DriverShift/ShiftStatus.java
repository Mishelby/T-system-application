package org.example.logisticapplication.domain.DriverShift;

public enum ShiftStatus {
    ON_SHIFT("ON_SHIFT"),
    REST("REST");

    private String statusName;

    ShiftStatus(String statusName){
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}

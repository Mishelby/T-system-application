package org.example.logisticapplication.domain.Driver;

public enum DriverStatus {
    DRIVING("DRIVING"),
    NOT_DRIVING("NOT_DRIVING"),
    THE_SECOND_DRIVER("THE_SECOND_DRIVER"),
    LOADING_UNLOADING_OPERATIONS("LOADING_UNLOADING_OPERATIONS");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getStatusName(){
        return displayName;
    }

    public static DriverStatus fromDisplayName(String name) {
        for (DriverStatus status : DriverStatus.values()) {
            if (status.getStatusName().equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid driver status: %s".formatted(name));
    }
}

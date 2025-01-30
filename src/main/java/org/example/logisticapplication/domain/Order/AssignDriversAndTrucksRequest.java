package org.example.logisticapplication.domain.Order;

import java.util.List;

public record AssignDriversAndTrucksRequest(
        List<Long> driverIds,
        List<Long> truckIds
) {
}

package org.example.logisticapplication.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.repository.TruckRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TruckValidHelper {
    private final TruckRepository truckRepository;
    @Getter
    private static final String DEFAULT_MESSAGE = "Truck does not exist with id=%s";
}

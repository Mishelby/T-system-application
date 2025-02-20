package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.repository.DriverStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverStatusService {
    private final DriverStatusRepository driverStatusRepository;

    @Transactional
    public synchronized ConcurrentMap<DriverStatus, DriverStatusEntity> getDriverStatusMap() {
        var allDriverStatus = driverStatusRepository.findAll().stream()
                .collect(Collectors.toMap(
                        DriverStatusEntity::getStatus,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new
                ));

        var driverStatuses = DriverStatus.values();

        if (allDriverStatus.size() < driverStatuses.length) {
            Arrays.stream(driverStatuses)
                    .filter(driverStatus -> !allDriverStatus.containsKey(driverStatus))
                    .forEach(driverStatus -> {
                        var driverStatusEntity = driverStatusRepository.save(new DriverStatusEntity(driverStatus));
                        allDriverStatus.put(driverStatus, driverStatusEntity);
                    });
        }

        return allDriverStatus;
    }

}

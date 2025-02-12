package org.example.logisticapplication.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.repository.DriverStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DriverStatusService {
    private final DriverStatusRepository driverStatusRepository;

    @PostConstruct
    @Transactional
    public void init() {
        var allStatus = Stream.concat(
                Arrays.stream(DriverStatus.values()).map(DriverStatus::getStatusName),
                Arrays.stream(ShiftStatus.values()).map(ShiftStatus::getStatusName)
        ).toList();

        for (String status : allStatus) {
            boolean exists = driverStatusRepository.existsDriverStatusByName(status);

            if(!exists) {
                driverStatusRepository.save(new DriverStatusEntity(status));
            }
        }

    }
}

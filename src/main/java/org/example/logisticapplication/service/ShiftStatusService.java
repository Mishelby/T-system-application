package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.domain.ShiftStatus.ShiftStatusEntity;
import org.example.logisticapplication.repository.ShiftStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftStatusService {
    private final ShiftStatusRepository shiftStatusRepository;

    @Transactional
    public synchronized ConcurrentMap<ShiftStatus, ShiftStatusEntity> getShiftStatusMap() {
        var allShiftStatus = shiftStatusRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ShiftStatusEntity::getStatus,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new
                ));

        var shiftStatuses = ShiftStatus.values();

        if (allShiftStatus.size() < shiftStatuses.length) {
            Arrays.stream(shiftStatuses)
                    .filter(shiftStatus -> !allShiftStatus.contains(shiftStatus))
                    .forEach(shiftStatus -> {
                        var savedShift = shiftStatusRepository.save(new ShiftStatusEntity(shiftStatus));
                        allShiftStatus.put(shiftStatus, savedShift);
                    });
        }

        return allShiftStatus;
    }
}

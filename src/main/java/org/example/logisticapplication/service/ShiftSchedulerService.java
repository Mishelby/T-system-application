package org.example.logisticapplication.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.DriverShift.DriverShift;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.DriverShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftSchedulerService {
    private final DriverRepository driverRepository;
    private final DriverShiftRepository driverShiftRepository;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

    public void autoCloseExpiredShifts(DriverShift driverShift) {
        executorService.schedule(() -> closeShift(driverShift), 12, TimeUnit.HOURS);
    }

    private void closeShift(DriverShift driverShift) {
        if (driverShift.getEndShift() != null) {
            log.info("Shift already closed for driver: " + driverShift.getDriver().getId());
            return;
        }

        try {
            var endShift = driverShift.getStartShift().plusHours(12);
            driverShift.setEndShift(endShift);

            var driverEntity = driverShift.getDriver();
            var totalWorkedHours = driverEntity.getNumberOfHoursWorked();
            totalWorkedHours += (int) Duration.between(driverShift.getStartShift(), endShift).toHours();
            driverEntity.setNumberOfHoursWorked(totalWorkedHours);

            driverShiftRepository.save(driverShift);
            driverRepository.save(driverEntity);

            log.info("Shift closed for driver: %s".formatted(driverEntity.getId()));
        } catch (Exception e) {
            log.warn("Error while closing shift: %s".formatted(e.getMessage()));
        }
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }
}


package org.example.logisticapplication.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.DriverShift.DriverShift;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.DriverShiftRepository;
import org.springframework.scheduling.annotation.Scheduled;
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

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void schedulePoolSizeUpdater() {
        executorService.scheduleAtFixedRate(
                this::updateExecutorServiceBasedOnDriversInShift,
                0,
                10,
                TimeUnit.MINUTES);
    }

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

    @Transactional
    public void updateExecutorServiceBasedOnDriversInShift() {
        Integer driversInShift = driverRepository.findDriversInShift(ShiftStatus.ON_SHIFT.name());
        int poolSize = Math.max(driversInShift, 1);

        executorService.shutdown();
        executorService = Executors.newScheduledThreadPool(poolSize);

        log.info("New pool size updated to: {}", poolSize);
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) { // Увеличенный таймаут
                log.warn("ExecutorService did not terminate in the specified time.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted during executor shutdown: {}", e.getMessage());
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }
}


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
                this::updateExecutorService,
                0,
                10,
                TimeUnit.MINUTES);
    }

    @Transactional
    public void updateExecutorService() {
        var driversInShift = driverRepository.findDriversInShift(ShiftStatus.ON_SHIFT.name());
        int poolSize = Math.max(driversInShift, 1);

        executorService.shutdown();
        executorService = Executors.newScheduledThreadPool(poolSize);

        log.info("New pool size updated to: {}", poolSize);
    }

    public void autoCloseExpiredShifts(
            DriverShift driverShift
    ) {
        executorService.schedule(() -> closeShift(driverShift), 12, TimeUnit.HOURS);
    }

    private void closeShift(DriverShift driverShift) {
        if (driverShift.getEndShift() != null) {
            log.info("Shift already closed for driver: {}", driverShift.getDriver().getId());
            return;
        }

        try {
            var endShift = driverShift.getStartShift().plusHours(12);
            driverShift.setEndShift(endShift);

            var driver = driverShift.getDriver();

            var workedMinutes = Duration.between(driverShift.getStartShift(), endShift).toMinutes();
            var extraHours = workedMinutes % 60 >= 30 ? 1 : 0;

            var totalWorkedHours = driver.getNumberOfHoursWorked() + (int) (workedMinutes / 60) + extraHours;
            driver.setNumberOfHoursWorked(totalWorkedHours);

            driverShiftRepository.save(driverShift);
            driverRepository.save(driver);

            log.info("Shift closed for driver: {}", driver.getId());
        } catch (Exception e) {
            log.error("Error while closing shift for driver {}: {}", driverShift.getDriver().getId(), e.getMessage(), e);
        }
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


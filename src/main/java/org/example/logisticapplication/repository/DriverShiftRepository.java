package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.DriverShift.DriverShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverShiftRepository extends JpaRepository<DriverShift, Long> {

    @Query("SELECT ds FROM DriverShift ds WHERE ds.driver.id = :driverId")
    Optional<DriverShift> findByDriverId(
            @Param("driverId") Long driverId
    );

}

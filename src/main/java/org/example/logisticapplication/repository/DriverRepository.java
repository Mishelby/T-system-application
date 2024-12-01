package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
    boolean existsByPersonNumber(String personNumber);

    boolean existsDriverEntityById(Long id);

    @Query("SELECT d FROM DriverEntity d WHERE d.currentTruck =:truck")
    List<DriverEntity> findAllByCurrentTruck(TruckEntity truck);

    @Query(value = """
            SELECT d
            FROM DriverEntity d
            LEFT JOIN d.currentCity c
            WHERE(:status IS NULL OR d.status =:status)
            AND(:cityName IS NULL OR c.name =:cityName)
            """)
    List<DriverEntity> findAllDrivers(
            @Param("status") String status,
            @Param("cityName") String cityName
    );
}

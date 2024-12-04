package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Query(value = """
            UPDATE drivers 
            SET name = CASE WHEN :name IS NOT NULL THEN :name ELSE name END,
                second_name = CASE WHEN :secondName IS NOT NULL THEN :secondName ELSE second_name END,
                person_number = CASE WHEN :personNumber IS NOT NULL AND person_number != :personNumber THEN :personNumber ELSE person_number END,
                driver_status= CASE WHEN :status IS NOT NULL THEN :status ELSE driver_status END,
                num_of_hours_worked = CASE WHEN :numberOfHoursWorked IS NOT NULL THEN :numberOfHoursWorked ELSE num_of_hours_worked END,
                truck_id = CASE WHEN :truckId IS NOT NULL THEN :truckId ELSE truck_id END,
                current_city_id = CASE WHEN :cityId IS NOT NULL THEN :cityId ELSE current_city_id END
            WHERE id = :id
            """, nativeQuery = true)
    void updateCurrentDriver(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("secondName") String secondName,
            @Param("personNumber") String personNumber,
            @Param("numberOfHoursWorked") Integer numberOfHoursWorked,
            @Param("status") String status,
            @Param("cityId") Long currentCityId,
            @Param("truckId") Long currentTruckId
    );
}

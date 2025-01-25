package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    @EntityGraph(attributePaths = {"currentCity", "currentTruck"})
    @Query("SELECT d FROM DriverEntity d")
    List<DriverEntity> findAllDrivers();

    @Query("""
            SELECT COUNT(*) = 0 
            FROM DriverEntity d 
            WHERE d.currentTruck.registrationNumber = :number
            AND d.personNumber <> :personNumber
            """)
    boolean existsTruckByDriverId(
            @Param("personNumber") Long personNumber,
            @Param("number") String number
    );

    boolean existsByPersonNumber(Long personNumber);

    @Modifying
    @Query("UPDATE DriverEntity d SET d.currentTruck = NULL WHERE d.currentTruck = :truck")
    void removeCurrentTruck(@Param("truck") TruckEntity truck);

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

    @Query("""
            SELECT COUNT(t) = 0 FROM TruckEntity t 
            LEFT JOIN TruckOrderEntity tor ON tor.id = t.id 
            WHERE t =: truck
            """)
    boolean isTruckInOrder(TruckEntity truck);

    @Modifying
    @Query(value = """
            UPDATE driver
            SET name = CASE WHEN :name IS NOT NULL THEN :name ELSE name END,
                last_name = CASE WHEN :lastName IS NOT NULL THEN :secondName ELSE last_name END,
                person_number = CASE WHEN :personNumber IS NOT NULL AND person_number != :personNumber THEN :personNumber ELSE person_number END,
                driver_status= CASE WHEN :status IS NOT NULL THEN :status ELSE driver_status END,
                num_of_hours_worked = CASE WHEN :numberOfHoursWorked IS NOT NULL THEN :numberOfHoursWorked ELSE num_of_hours_worked END,
                current_truck_id = CASE WHEN :truckId IS NOT NULL THEN :truckId ELSE current_truck_id END,
                current_city_id = CASE WHEN :cityId IS NOT NULL THEN :cityId ELSE current_city_id END
            WHERE id = :id
            """, nativeQuery = true)
    void updateCurrentDriver(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("secondName") String lastName,
            @Param("personNumber") Long personNumber,
            @Param("numberOfHoursWorked") Integer numberOfHoursWorked,
            @Param("status") String status,
            @Param("cityId") Long currentCityId,
            @Param("truckId") Long currentTruckId
    );

    @EntityGraph(attributePaths = {
            "currentTruck",
            "currentTruck.drivers",
            "personNumber",
            "currentTruck.registrationNumber",
    })
    @Query("""
                SELECT d
                FROM DriverEntity d
                WHERE d.id IN (
                    SELECT dor.driver.id
                    FROM DriverOrderEntity dor
                    WHERE dor.order.id = :orderId
                )
            """)
    List<DriverEntity> findDriversForOrderId(
            @Param("orderId") Long orderId
    );

    @Modifying
    @Query(value = """
            UPDATE driver
            SET driver_status = :newStatus
            WHERE id = :driverId
              AND id IN (SELECT dor.driver_id
                         FROM driver_order dor
                         WHERE dor.driver_id = driver.id)
            """, nativeQuery = true)
    void changeShiftForDriverById(
            @Param("driverId") Long driverId,
            @Param("newStatus") String status
    );

    @Modifying
    @Query(value = """
            UPDATE driver
            SET driver_status = :newStatus
            WHERE id = :driverId
              AND id IN (SELECT dor.driver_id
                         FROM driver_order dor
                         WHERE dor.driver_id = driver.id)
            """, nativeQuery = true)
    void changeDriverStatusById(
            @Param("driverId") Long driverId,
            @Param("newStatus") String status
    );


    @EntityGraph(attributePaths = {"currentCity", "currentCity.countryMap"})
    @Query(value = """
            SELECT d 
            FROM DriverEntity d    
                     LEFT JOIN TruckEntity t ON d.currentTruck.id = t.id
                     LEFT JOIN DriverOrderEntity dor ON d.id = dor.driver.id
            WHERE dor.driver.id IS NULL
              AND d.currentCity.id IN (:truckId)
              AND (d.numberOfHoursWorked + (:distance / :speed) < :total)
            """)
    List<DriverEntity> findDriversByTruckId(
            @Param("truckId") Set<Long> truckId,
            @Param("distance") Long distance,
            @Param("speed") Long speed,
            @Param("total") Long total
    );

    @Query("""
            SELECT COUNT(d) 
            FROM DriverEntity d 
            WHERE d.status = :status
            """)
    Integer findDriversInShift(
            @Param("status") String status
    );

}

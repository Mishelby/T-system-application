package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverAndTruckDto;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverWithTruckDto;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    @EntityGraph(attributePaths = {"currentCity", "currentTruck"})
    @Query("SELECT d FROM DriverEntity d")
    List<DriverEntity> findAllDrivers();

    @Query("""
            SELECT d 
            FROM DriverEntity d
            JOIN FETCH DriverOrderEntity doe ON d.id = doe.driver.id
            WHERE doe.order.id = :orderId            
            """)
    List<DriverEntity> findAllByOrderId(Long orderId);

    boolean existsByPersonNumber(Long personNumber);

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

    @Query(value = """
             SELECT d.id,
                    d.name,
                    d.num_of_hours_worked,
                    d.person_number,
                    d.last_name,
                    d.driver_status,
                    d.current_truck_id,
                    d.current_city_id
             FROM driver d
                      LEFT JOIN truck t ON d.current_truck_id = t.id
                      LEFT JOIN driver_order dor ON d.id = dor.driver_id
             WHERE d.current_city_id = :cityId
               AND (t.current_city_id IS NULL OR d.current_city_id = t.current_city_id)
               AND dor.driver_id IS NULL
               AND d.num_of_hours_worked + (SELECT DISTINCT d.distance total_distance
                                            FROM route_point rp
                                                     LEFT JOIN distance d ON d.from_city_id = (
                                                                 SELECT rp2.city_id
                                                                 FROM route_point rp2
                                                                 WHERE rp2.operation_type = 'LOADING'
                                                                 AND rp2.order_id = :orderId)
                                                     LEFT JOIN orders ords ON ords.id = rp.order_id
                                            WHERE ords.id = :orderId
                                              AND d.to_city_id = (SELECT rp3.city_id
                                                                  FROM route_point rp3
                                                                  WHERE rp3.operation_type = 'UNLOADING'
                                                                  AND rp3.order_id = :orderId)) / :averageSpeed < :numberOfHoursWorkedLimit;
            """, nativeQuery = true)
    List<DriverEntity> findDriversForCorrectTruck(
            @Param("cityId") Long cityId,
            @Param("orderId") Long orderId,
            Double averageSpeed,
            Integer numberOfHoursWorkedLimit
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


    @Query("SELECT d FROM DriverEntity d WHERE d.id IN(:driverIds)")
    List<DriverEntity> findAllDriversById(
            @Param("driverIds") Set<Long> driverIds
    );

    @Query("""
            SELECT new org.example.logisticapplication.domain.Driver.DriverWithTruckDto(d, t)
            FROM DriverEntity d 
            LEFT JOIN FETCH d.currentTruck t 
            WHERE d.id = :driverId
            """)
    Optional<DriverWithTruckDto> findDriverWithTruckById(
            @Param("driverId") Long driverId
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

}

package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<TruckEntity, Long> {

    @EntityGraph(attributePaths = {"currentCity", "drivers"})
    @Query("SELECT t FROM TruckEntity t")
    List<TruckEntity> findAllTrucks();

    @Query("SELECT COUNT(t) > 0 FROM TruckEntity t WHERE t.registrationNumber = :registrationNumber")
    boolean existsByRegistrationNumber(String registrationNumber);


    @Query("SELECT t FROM TruckEntity t WHERE :currentDriver MEMBER OF t.drivers")
    List<TruckEntity> findAllByCurrentDriver(DriverEntity currentDriver);

    @Modifying
    @Query(value = """
            UPDATE truck
            SET  
            reg_number = CASE WHEN :registrationNumber IS NOT NULL THEN :registrationNumber ELSE reg_number END,
            size_of_driver_shift = CASE WHEN :driversShift IS NOT NULL THEN :driversShift ELSE size_of_driver_shift END,
            condition = CASE WHEN :truck_condition IS NOT NULL THEN :truck_condition ELSE condition END,
            capacity = CASE WHEN :capacity IS NOT NULL THEN :capacity ELSE capacity END,
            current_city_id = CASE WHEN :cityId IS NOT NULL THEN :cityId ELSE current_city_id END
            WHERE id = :id
            """
            , nativeQuery = true)
    void updateTruckById(
            @Param("id") Long id,
            @Param("registrationNumber") String registrationNumber,
            @Param("driversShift") Integer driversShift,
            @Param("truck_condition") String status,
            @Param("capacity") Double capacity,
            @Param("cityId") Long currentCityId
    );

    @EntityGraph(attributePaths = {"currentCity", "drivers"})
    @Query(value = """
            SELECT t
            FROM TruckEntity t
                     LEFT JOIN TruckOrderEntity tor ON t.id = tor.truck.id
            WHERE t.currentCity.id = :cityId
              AND t.status = :condition
              AND tor.truck.id IS NULL
              AND t.capacity >= :weight
            """)
    List<TruckEntity> findTrucksForOrderByWeight(
            @Param("cityId") Long cityId,
            @Param("condition") String condition,
            @Param("weight") BigDecimal weight
    );
}

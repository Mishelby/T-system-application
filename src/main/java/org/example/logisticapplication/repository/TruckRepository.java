package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.Truck.TruckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TruckRepository extends JpaRepository<TruckEntity, Long> {

    @Query("SELECT COUNT(t) > 0 FROM TruckEntity t WHERE t.registrationNumber = :registrationNumber")
    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<TruckEntity> findTruckEntityByRegistrationNumber(String registrationNumber);

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

    @Query("""
            SELECT t FROM TruckEntity t 
                        LEFT JOIN TruckOrderEntity toe ON t.id = toe.truck.id        
                        WHERE t.status = :status
                        AND toe.truck.id IS NULL
            """)
    List<TruckEntity> findAllInCurrentCity(
            @Param("status") String status
    );
}

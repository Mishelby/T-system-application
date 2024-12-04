package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<TruckEntity, Long> {

    @Query("SELECT COUNT(t) > 0 FROM TruckEntity t WHERE t.registrationNumber = :registrationNumber")
    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<TruckEntity> findTruckEntityByRegistrationNumber(String registrationNumber);

    @Query("SELECT t FROM TruckEntity t WHERE :currentDriver MEMBER OF t.drivers")
    List<TruckEntity> findAllByCurrentDriver(DriverEntity currentDriver);

    @Modifying
    @Query(value = """
            UPDATE trucks 
            SET  
            reg_number = CASE WHEN :registrationNumber IS NOT NULL THEN :registrationNumber ELSE reg_number END,
            drivers_shift = CASE WHEN :driversShift IS NOT NULL THEN :driversShift ELSE drivers_shift END,
            status = CASE WHEN :status IS NOT NULL THEN :status ELSE status END,
            capacity = CASE WHEN :capacity IS NOT NULL THEN :capacity ELSE capacity END,
            city_id = CASE WHEN :cityId IS NOT NULL THEN :cityId ELSE city_id END
            WHERE id = :id
            """
    , nativeQuery = true)
    void updateTruckById(
            @Param("id") Long id,
            @Param("registrationNumber") String registrationNumber,
            @Param("driversShift") Integer driversShift,
            @Param("status") String status,
            @Param("capacity") Double capacity,
            @Param("cityId") Long currentCityId
    );
}

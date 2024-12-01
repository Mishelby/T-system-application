package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}

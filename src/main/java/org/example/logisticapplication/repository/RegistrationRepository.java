package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RegistrationRepository extends JpaRepository<DriverEntity, Long> {

    @Query("""
            SELECT (COUNT(d) > 0) 
            FROM DriverEntity d 
            WHERE d.name = :driverName 
            AND d.personNumber = :driverNumber
            """)
    boolean checkDriverInSystem(
            @Param("driverName") String driverName,
            @Param("driverNumber") Long driverNumber
    );

}

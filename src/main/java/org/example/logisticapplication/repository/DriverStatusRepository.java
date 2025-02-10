package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverStatusRepository  extends JpaRepository<DriverStatusEntity, Long> {

    @Query("SELECT (COUNT(*) > 0) FROM DriverStatusEntity ds WHERE ds.status = :status")
    boolean existsDriverStatusByDisplayName(@Param("status")String displayName);
}

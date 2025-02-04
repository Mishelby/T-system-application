package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRepository extends JpaRepository<DistanceEntity, Long> {
}

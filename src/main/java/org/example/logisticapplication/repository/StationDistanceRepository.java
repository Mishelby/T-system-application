package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.StationDistance.StationDistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationDistanceRepository extends JpaRepository<StationDistanceEntity, Long> {
}

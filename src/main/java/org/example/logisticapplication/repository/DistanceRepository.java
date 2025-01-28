package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceRepository extends JpaRepository<DistanceEntity, Long> {

    @Query("""
            SELECT d
            FROM DistanceEntity d
            WHERE d.fromCity.id = :cityId1 
            AND d.toCity.id = :cityId2 
            AND d.distance = :distance
            """)
    DistanceEntity findDistanceByCitiesAndDistance(
            Long cityId1,
            Long cityId2,
            Long distance
    );

}

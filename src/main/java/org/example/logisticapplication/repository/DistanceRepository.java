package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistanceRepository extends JpaRepository<DistanceEntity, Long> {

    @Query("""
            SELECT (COUNT(d) > 0)
            FROM DistanceEntity d
            WHERE d.fromCity.id IN (:cityId) 
            AND d.toCity.id IN (:cityId)
            AND d.distance = :distance
            """)
    boolean isExistsDistanceEntity(
            List<Long> cityId,
            Long distance
    );

    @Query("""
            SELECT DISTINCT d 
            FROM DistanceEntity d
            LEFT JOIN  OrderDistanceEntity ode
            WHERE ode.order.id = :orderId
            """)
    Optional<DistanceEntity> findDistancetByOrderId(
            @Param("orderId") Long orderId
    );

}

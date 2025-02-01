package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Distance.DistanceEntity;
import org.example.logisticapplication.domain.OrderDistanceEntity.OrderDistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDistanceRepository extends JpaRepository<OrderDistanceEntity, Long> {

    @Query("""
            SELECT d
            FROM OrderDistanceEntity od  
            LEFT JOIN FETCH DistanceEntity d ON d.id = od.distance.id
            WHERE od.order.id = :orderId          
            """)
    Optional<DistanceEntity> findDistanceEntityByOrder(Long orderId);
}

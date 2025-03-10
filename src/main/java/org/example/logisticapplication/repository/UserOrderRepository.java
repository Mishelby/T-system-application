package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrderEntity, Long> {

    @EntityGraph(attributePaths = {"user", "order"})
    @Query("""
            SELECT o
            FROM UserOrderEntity o
            WHERE o.order.id= :id            
            """)
    Optional<UserOrderEntity> findByOrderId(
            @Param("id") Long orderId
    );

    @Query("""
            SELECT uoe
            FROM UserOrderEntity uoe
            WHERE uoe.user.id= :id                        
            """)
    List<UserOrderEntity> findByUserId(Long id);
}

package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.Order.OrderStatusDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query(value = """
            SELECT new org.example.logisticapplication.domain.Order.OrderStatusDto(o.uniqueNumber,o.status)
            FROM OrderEntity o
            WHERE o.id = :order_id
            """)
    OrderStatusDto showOrderStatusByOrderId(@Param("order_id") Long orderId);

    @EntityGraph(attributePaths = {"countryMap"})
    @Query(value = """
            SELECT o
            FROM OrderEntity o
            INNER JOIN FETCH o.routePoints rp  
            LEFT JOIN FETCH rp.cargo c           
            LEFT JOIN FETCH o.driverOrders             
            LEFT JOIN FETCH o.truckOrders             
            """)
    List<OrderEntity> findLast(Pageable pageable);

    @EntityGraph(attributePaths = {"countryMap"})
    @Query("""
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH DriverOrderEntity doe ON o.id = doe.order.id
            WHERE doe.driver.id = :driverId
            """)
    Optional<OrderEntity> findOrderEntitiesByDriverId(
            @Param("driverId") Long driverId
    );
}

package org.example.logisticapplication.repository;

import org.aspectj.weaver.ast.Or;
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

    @Query("""
            SELECT DISTINCT o 
            FROM OrderEntity o  
            LEFT JOIN FETCH DriverOrderEntity dor ON dor.order.id = o.id
            LEFT JOIN FETCH TruckOrderEntity tor ON tor.order.id = o.id
            LEFT JOIN FETCH DriverEntity driver ON driver.id = dor.driver.id
            LEFT JOIN FETCH TruckEntity truck ON truck.id = tor.truck.id          
            WHERE dor.driver.id IS NOT NULL
            AND tor.order.id IS NOT NULL                                 
            """)
    List<OrderEntity> findAllCurrentOrders();

    @EntityGraph(attributePaths = {"countryMap"})
    @Query("""
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH DriverOrderEntity doe ON o.id = doe.order.id
            WHERE doe.driver.id = :driverId
            """)
    Optional<OrderEntity> findOrderEntitiesByDriverId(
            @Param("driverId") Long driverId
    );

    @EntityGraph(attributePaths = {"countryMap", "countryMap.cities"})
    @Query("""
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH DriverOrderEntity doe ON o.id = doe.order.id
            LEFT JOIN FETCH TruckOrderEntity toe ON o.id = toe.order.id
            WHERE doe.driver.id IS NULL AND toe.truck.id IS NULL
            ORDER BY o.id ASC
            """)
    List<OrderEntity> findOrdersForSubmit(Pageable pageable);

    @Query("SELECT o FROM OrderEntity o WHERE o.uniqueNumber = :number")
    Optional<OrderEntity> findOrderEntityByNumber(
            @Param("number") String number
    );
}

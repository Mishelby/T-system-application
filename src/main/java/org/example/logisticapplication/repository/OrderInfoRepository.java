package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.OrderInfo.OrderInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfoEntity, Long> {

    @Query("""
            SELECT c.cityFrom
            FROM OrderInfoEntity c 
            WHERE c.order.id = :orderId                      
            """)
    Optional<CityEntity> findCityFrom(Long orderId);

    @Query("""
            SELECT oi
            FROM OrderInfoEntity oi
            WHERE oi.order.id = :orderId                        
            """)
    Optional<OrderInfoEntity> findOrderInfoByOrderId(Long orderId);
}

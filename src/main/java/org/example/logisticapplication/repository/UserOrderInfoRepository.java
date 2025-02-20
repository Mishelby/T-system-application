package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.UserOrderInfo.UserOrderInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserOrderInfoRepository extends JpaRepository<UserOrderInfoEntity, Long> {
    @Query("""
            SELECT uoi 
            FROM UserOrderInfoEntity uoi
            WHERE uoi.userName = :userName   
            AND uoi.orderNumber = :orderNumber                  
            """)
    Optional<UserOrderInfoEntity> findUserOrderInfo(
            @Param("userName") String userName,
            @Param("orderNumber") String orderNumber
    );

    @Query("""
            SELECT oi.desiredDate
            FROM UserOrderInfoEntity oi  
            WHERE oi.orderNumber = :orderNumber                      
            """)
    String findDesiredDateForOrder(String orderNumber);
}

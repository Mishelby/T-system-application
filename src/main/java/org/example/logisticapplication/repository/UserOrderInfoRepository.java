package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.UserOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserOrderInfoRepository extends JpaRepository<UserOrderInfo, Long> {

    @Query("""
            SELECT uoi 
            FROM UserOrderInfo uoi
            WHERE uoi.userName = :userName   
            AND uoi.orderNumber = :orderNumber                  
            """)
    Optional<UserOrderInfo> findUserOrderInfo(
            @Param("userName") String userName,
            @Param("orderNumber") String orderNumber
    );
}

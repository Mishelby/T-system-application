package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsOrderEntityByUniqueNumber(String uniqueNumber);

    @Query("SELECT COUNT(*) > 0 FROM OrderEntity oe WHERE oe.uniqueNumber = :uniqueNumber")
    boolean existsByUniqueNumber(
            @Param("uniqueNumber") String uniqueNumber
    );

    @Query("SELECT oe FROM OrderEntity oe WHERE oe.uniqueNumber = :uniqueNumber")
    OrderEntity findByUniqueNumber(@Param("uniqueNumber") String uniqueNumber);
}

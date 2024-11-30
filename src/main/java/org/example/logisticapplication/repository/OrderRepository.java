package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsOrderEntityByUniqueNumber(String uniqueNumber);
}

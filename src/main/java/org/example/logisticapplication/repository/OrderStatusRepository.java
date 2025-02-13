package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.OrderStatusEntity.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {
}

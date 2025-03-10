package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.OrderTimeEntity.OrderTimeEntity;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@ReflectiveScan
public interface OrderTimeRepository extends JpaRepository<OrderTimeEntity, Long> {
    Optional<OrderTimeEntity> findOrderTimeEntityByOrderNumber(String orderNumber);
}

package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrderEntity, Long> {
}

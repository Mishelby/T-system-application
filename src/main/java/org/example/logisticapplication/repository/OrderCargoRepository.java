package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.OrderCargo.OrderCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCargoRepository extends JpaRepository<OrderCargo, Long> {
}

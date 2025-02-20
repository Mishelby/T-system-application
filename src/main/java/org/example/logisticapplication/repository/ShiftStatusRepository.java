package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.ShiftStatus.ShiftStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface ShiftStatusRepository extends JpaRepository<ShiftStatusEntity, Long> {
}

package org.example.logisticapplication.repository;


import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePointEntity, Long> {

}

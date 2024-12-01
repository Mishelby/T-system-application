package org.example.logisticapplication.repository;


import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RoutePointRepository extends JpaRepository<RoutePointEntity, Long> {

    @Query("SELECT COUNT(r) > 0 FROM RoutePointEntity r WHERE r.id =:routePointId")
    boolean existsRoutePointEntitiesById(@Param("routePointId") Long routePointId);

}

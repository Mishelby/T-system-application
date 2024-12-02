package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Long> {

    boolean existsByCargoNumber(Integer cargoNumber);

    @Query("""
            SELECT DISTINCT c FROM CargoEntity c
            JOIN RoutePointEntity rp ON rp.cargo.id = c.id
            JOIN OrderEntity order ON order.id = rp.id
            WHERE order.id = :id
            AND rp.operationType = 'LOADING' 
            AND rp.operationType = 'UNLOADING'                    
            """)
    List<CargoEntity> findCargoEntityByCorrectStatus(
            @Param("id") Long id
    );
}
